#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <signal.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <sys/mman.h>

#define FIFO "fifo"
#define CONFIG "config"
#define SLEEP 10*1000
#define SHM_KEY 48574
#define SHM_KEY_MENU 43211
#define sciezka "/dev/urandom"

#define S1 20
#define S2 2
#define S3 3
#define S4 4
#define S5 5
#define S6 6

int ch[3], EX=1, stop=0, ids[4];
int shmid;
char *buffor_shm;

void handler_pass_to_childs(int x) {

  int i;
  for (i = 0; i < 3; i++)
    kill(ch[i], x);
}

void handler_stop(int x) {
	stop=1;
}

void handler_continue(int x) {
	stop=0;
}

void handler_EX(int x) {
	EX=0;
}

void handler_pass_to_ch1(int x) {	//handler main
	kill(ch[0], x);
}

void handler_pass_to_main(int x) {
	kill(getppid(), x);
}

void handler_exit(int x) {
	int i;
	for(i=0;i<3;i++)
	kill(ch[i], SIGKILL);
	unlink(FIFO);
	unlink(CONFIG);
	shmdt(buffor_shm);
	shmctl(shmid, IPC_RMID, NULL);
	kill(getpid(), SIGKILL);
}

void clear_buff()
{
    while (getchar() != '\n');
}

void clear_array(char* array, size_t size)
{
	int i=0;
    while (i<size)
    {
		array[i]=0;
		i++;
	}
}

void get_usr_data(char* buf, size_t size)
{
	size_t i = 0;
	printf("\nPut Your data:	\n");
	while (i < size)
	{
		scanf("%c", &buf[i]);
		if(buf[i] == '\n' || buf[i] == ' ')
		{ 
			continue;
		}
		else {
			i++;
		}
		
	}
	//clear_buff();
}

void print_char_hex(char* str, size_t size)
{
	size_t i = 0;
	while (i < size)
	{
		printf("%.2s ", str + i);
		i += 2;
	}
	printf("\n");
}

void print_char_normal(char* string, size_t size)
{
	unsigned int i = 0;
	while (i < size)
	{
		if (string[i] >= 0 && string[i] <= 31)
		{
			printf("^%c ", string[i] + 64);
		}
		else if (string[i] == 127)
		{
			printf("^? ");
		}
		else
		{
			printf("%c  ", string[i]);
		}
		i++;
	}
	printf("\n");
}

int main(int argc, char* argv[]) {
	
	int opt=1;
	sigset_t new_set;
	if(argc>1) sscanf(argv[1], "%d", &opt);
	int a, b;
	char* path=sciezka;
	int fd_read, fd_fifo_write, fd_fifo_read, fd_f5_write;
	char buffor[15+1], hex_buffor[30+1];
	
	if(opt==2 && argc>2) path=argv[2];
	
	shmid = shmget(SHM_KEY, 2 * 15 * sizeof(char), IPC_CREAT | 0666);
    if (shmid == -1) {
        perror("shmget error\n");
        exit(1);
    }

    buffor_shm = shmat(shmid, NULL, 0);
    if (buffor_shm == NULL) {
        perror("shmat error\n");
        exit(1);
    }	
	
	mkfifo(FIFO, 0666);
	
    if ((ch[0] = fork()) == 0) {									//P1
		
		sigfillset(&new_set);
		sigdelset(&new_set, S1);
		sigdelset(&new_set, S2);
		sigdelset(&new_set, S3);
		sigdelset(&new_set, S4);
		sigdelset(&new_set, S5);
		sigdelset(&new_set, S6);
		sigprocmask(SIG_SETMASK, &new_set, NULL);
		signal(S1, handler_pass_to_main);
		signal(S2, handler_stop);
		signal(S3, handler_continue);
		signal(S4, handler_EX);
		signal(S5, SIG_IGN);
		signal(S6, SIG_IGN);
		
		
		if(opt==2 || opt==3) {
			if ((fd_read=open(path, O_RDONLY)) < 0) {
				perror("Error while opening a file - p1\n");
				kill(getppid(), SIGINT);
				while(1);
			}
		}
		

		EX=0;
		while (1) {
			
			while(EX) pause();
//			while(EX);

			if(stop) {
				printf("Stop!\n");	
				while(stop) pause();
				printf("~Stop!\n");	
			}
			
			clear_array(buffor,15);
			
			if(opt==2 || opt==3) {
				if(read(fd_read, buffor,15)==0) {
					perror("\nError read p1\n");
					kill(getppid(), SIGINT);
				}
			}
			
			if(opt==1) {
				get_usr_data(buffor,15);
			}
			
			
			fd_fifo_write=open(FIFO, O_WRONLY);
			
			printf("1 Test p1: buffor\t=\t");
			print_char_normal(buffor,15);
			
			if(write(fd_fifo_write, buffor, 15)<=0) perror("P1 fifo write");
			close(fd_fifo_write);
			
			EX=1;
			kill(getppid(), S5);		
			
		}
		close(fd_read);
		
	} else if((ch[1] = fork()) == 0) {							//P2
		
		sigfillset(&new_set);
		sigdelset(&new_set, S1);
		sigdelset(&new_set, S2);
		sigdelset(&new_set, S3);
		sigdelset(&new_set, S4);
		sigdelset(&new_set, S5);
		sigdelset(&new_set, S6);
		sigprocmask(SIG_SETMASK, &new_set, NULL);
		signal(S1, handler_pass_to_main);
		signal(S2, handler_pass_to_main);
		signal(S3, handler_pass_to_main);
		signal(S4, SIG_IGN);
		signal(S5, handler_EX);
		signal(S6, SIG_IGN);
		
		EX=1;
		while (1) {
			
			clear_array(hex_buffor,30);
			
			fd_fifo_read=open(FIFO, O_RDONLY);
			
            read(fd_fifo_read, buffor, 15);
			close(fd_fifo_read);
			
            for (a = 0, b = 0; b < 30; ++a, b += 2) {
                sprintf(hex_buffor + b, "%02x", buffor[a] & 0xff);
            }
			
			printf("2 Test p2: hex_buffor\t=\t");
            print_char_hex(hex_buffor, 30);
            
            while(EX) pause();
                        
			clear_array(buffor_shm,30);
            memcpy(buffor_shm, hex_buffor, 2 * 15 * sizeof(char));
            printf("2 Test p2: buffor_shm\t=\t");
            print_char_hex(buffor_shm, 30);
			
			
			EX=1;
			kill(getppid(), S6);
		}
		
	} else if((ch[2] = fork()) == 0) {							//P3
		
		sigfillset(&new_set);
		sigdelset(&new_set, S1);
		sigdelset(&new_set, S2);
		sigdelset(&new_set, S3);
		sigdelset(&new_set, S4);
		sigdelset(&new_set, S5);
		sigdelset(&new_set, S6);
		sigprocmask(SIG_SETMASK, &new_set, NULL);
		signal(S1, handler_pass_to_main);
		signal(S2, handler_pass_to_main);
		signal(S3, handler_pass_to_main);
		signal(S4, SIG_IGN);
		signal(S5, SIG_IGN);
		signal(S6, handler_EX);
		
		EX=1;
		while (1) {
			
			while(EX) pause();
//			while(EX);
			clear_array(hex_buffor,30);
		
			printf("3 Test p3: buffor_shm\t=\t");
            print_char_hex(buffor_shm, 30);
            memcpy(hex_buffor, buffor_shm, 2 * 15 * sizeof(char));
            printf("3 Test p3: hex_buffor\t=\t");
            print_char_hex(hex_buffor,30);
            
            EX=1;
            kill(getppid(), S4);

            usleep(SLEEP);
		}
		
		
	} else {
		//mkfifo(FIFO_na_5, 0666);
		if((fd_f5_write=open(CONFIG, O_CREAT | O_WRONLY | O_APPEND, 0666))==-1) {
			printf("fd=-1");
			kill(getpid(), S1);
		}
		
		ids[0]=getpid();
		ids[1]=ch[0];
		ids[2]=ch[1];
		ids[3]=ch[2];
			
		if(write(fd_f5_write, ids, sizeof(ids))<=0) perror("fd_f5 write");
		close(fd_f5_write);
		
		printf("%d\n", getpid());
		printf("%d\n", ch[0]);
		printf("%d\n", ch[1]);
		printf("%d\n", ch[2]);
		
		sigfillset(&new_set);
		sigdelset(&new_set, S1);
		sigdelset(&new_set, S2);
		sigdelset(&new_set, S3);
		sigdelset(&new_set, S4);
		sigdelset(&new_set, S5);
		sigdelset(&new_set, S6);
		sigprocmask(SIG_SETMASK, &new_set, NULL);
		signal(S1, handler_exit);
		signal(S2, handler_pass_to_ch1);
		signal(S3, handler_pass_to_ch1);
		signal(S4, handler_pass_to_childs);
		signal(S5, handler_pass_to_childs);
		signal(S6, handler_pass_to_childs);
		
		//kill(ch[0], S4);
		
		while(1) {
			
		}
		
	}
    
    
    return 0;
    
}
