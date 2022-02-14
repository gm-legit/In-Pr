#include <iostream>
#include <Winbgim.h>

using namespace std;
void dywan(int x, int y, int a)
{
	int b=a/3;
	setfillstyle(1,3);
	bar(x+b,y+b,x+2*b,y+2*b);
	
	if(b>3)
	{
		dywan(x,y,b);
		dywan(x+b,y,b);
		dywan(x+2*b,y,b);
		
		dywan(x,y+b,b);
		dywan(x+2*b,y+b,b);
		
		dywan(x,y+2*b,b);
		dywan(x+b,y+2*b,b);
		dywan(x+2*b,y+2*b,b);
		
	}
}

int main(int argc, char** argv) 
{
	initwindow(500,500);
	cleardevice();
	setfillstyle(1,4);
	bar(0,0,500,500);
	dywan(0,0,500);
	while(!kbhit());
	
	return 0;
}
