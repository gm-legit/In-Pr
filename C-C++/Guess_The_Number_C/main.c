#include <stdio.h>
#include <time.h>
#include <stdlib.h>

int main()
{
    int r, w, i;
    int tab[3]={1,2,3};
    srand(time(NULL));
    r=rand()%100+1;
    time_t start, stop;
    printf("Podaj liczbe z przedzialu [0,100]: \n");
    scanf("%d", &w);
   
    time(&start);
   
    for(i=0; w!=r; i++)
    {
        if(w<r) printf("Podana liczba jest za mala\n");
        else printf("Podana liczba jest za duza\n");
       
        printf("Podaj inna liczbe: \n");
        scanf("%d", &w);
    }
   
    time(&stop);
   
    time_t ile_czasu = stop-start;
    printf("Liczba jest poprawna Brawo. Statystyki: \n");
    printf("ilosc prob: %d czas: %d s ", i+1, ile_czasu);
   
    return 0;
}