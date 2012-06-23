
typedef struct {
      char part1[100];
      char part2[100];
      char part3[100];
} stuff_;

typedef struct {
      char part1[100];
      char part2[100];
      char part3[100];
} something_;

typedef struct {
      char type[100];
      char date[100];
      char time[100];
      stuff_ stuff;
      something_ something;
} container_;

typedef struct {
      char text[512];
      container_ container[1];
} message_;

TEST_API void Connect(message_ *Pmessage)
{
       char temp [512] ;

       printf("*********** START test **************\n");
       strcpy (temp, Pmessage->text);

       printf("text:%s\n",Pmessage->text);
       strcpy(Pmessage->container[0].type,"Type1");
       printf("Type1=%s\n",Pmessage->container[0].type);

       strcpy(Pmessage->text,"Completed test");

       printf("*********** END test**************\n");
}
