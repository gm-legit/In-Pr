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

#define CONFIG "config"

int ids[4];

int main(int argc, char* argv[]) {
	
	pid_t pid;
	int sig_num, fd_f5_read, stop=0;
	
	while(!stop) {
		
		if((fd_f5_read=open(CONFIG, O_RDONLY))==-1) {
			perror("read error");
			exit(1);
		 }
		
		read(fd_f5_read, ids, 16);
		close(fd_f5_read);
	
		printf("Stop - send 20\n");
		printf("Continue - send 3\n");
		printf("Pause - send 2\n");
		printf("End loop - 1\n");
		printf("Enter a number to select a signal: ");
		scanf("%d", &sig_num);

		printf("Enter the number of the process (1, 2 or 3): ");
		scanf("%d", &pid);
		
		if(pid == 1) pid=ids[1];
		else if(pid == 2) pid=ids[2];
		else if(pid == 3) pid=ids[3];


		if (sig_num == 20) {
			kill(pid, 20);
		}
		else if (sig_num == 2) {
			kill(pid, 2);
		}
		else if (sig_num == 3) {
			kill(pid, 3);
		} 
		else if (sig_num == 1) {
			stop=1;
			break;
		} else {
			printf("Invalid signal number\n");
		}
	}
	
	return 0;
    
}
