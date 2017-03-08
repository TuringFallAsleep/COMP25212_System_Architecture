#include <stdio.h>

/*
  Cache Timing Test

  Access a (user-configurable) number of data structures
  distributed through memory

  and time report the run-time taken

*/


int nStructs = 2000000;		/* how many structures to keep accessing - defines working set */


/*********************************************************************************************

                                      THE TEST

*********************************************************************************************/


struct entry {			/* this is the data structure we access */
  struct entry* next;		/* a pointer to the next structure */
  int padding[8];		/* and some extra variables */
};

int maxstructs = 16777216/sizeof(struct entry);

/* allocate at least 16 million bytes of RAM
   in case there's a really HUGE cache */

struct entry *base;		/* pointer to table of data structures */

static struct entry *testPtr;	/* this pointer is used to follow the linked list of structures */

static int remaining = 0;	/* countdown variable - count # of structures accessed */

/*
  INITIALIZATION
*/

#include <stdlib.h>

void
initialize() {
  int i;
  static int stride = 5;	/* PLEASE CHOOSE A PRIME STRIDE!!! */
  base = (struct entry *) calloc(maxstructs, sizeof(struct entry)); /* allocate memory area
								       see "man 3 calloc" */
  for (i = 0; i < (maxstructs-1); i++) {
    int j = (i*stride) % maxstructs;
    int k = (j+stride) % maxstructs;
    base[j].next = &(base[k]);		/* link j'th struct to k'th struct */
    base[k].next = &(base[0]);		/* strictly only for very last entry:
					   ensure that final entry links
					   back to the first */
  }
}

/*
  THE TIMED TEST
*/

void
runtest() {			/* access next entry in linked list - 
				   keep following the list until we've accessed nStructs
				*/
  if (remaining == 0) {		/* if we've accessed enough entries */
    testPtr = &(base[0]);	/* reinitialize the pointer - start at beginning */
    remaining = nStructs;	/* and reset the counter */
  }
  remaining = remaining - 1;	/* keep count */
  testPtr = testPtr->next;	/* and --ACCESS THE DATA STRUCTURE-- */
}


/*
  TIMING INFRASTRUCURE
*/

#include <sys/resource.h>

double
getutime() {			/* returns real number, giving seconds of
				   user time consumed by this process */
  struct rusage ru;
  getrusage(RUSAGE_SELF, &ru);
  return (1.0 * ru.ru_utime.tv_sec + (0.000001 * ru.ru_utime.tv_usec));
}	  

/* run the test "N" times, and return the time (in real seconds) this takes */

double
runit(int n) {
  double t1 = getutime();
  int i;
  for (i = 0; i < n; i++) {
    runtest();			/* access next element */
  }
  return (getutime()-t1);
}

/* MAIN PROGRAM */

/*
  two-step process to calibrate run time:

  a) run test enough times to take at least 0.1 seconds
  b) then run the test for an estimate 1 second

  and report run time per iteration
*/

int initial = 10000;			 /* how many times to try running loop at first */

int
main(int argc, char **argv) {
  double runtime;
  int iterations;

  initialize();
  
  runtime = runit(initial);

  while(runtime < 0.1) {
    initial += initial;
    runtime = runit(initial);
  }

  printf("%d iterations take %7.5f seconds\n", initial, runtime);

  iterations = 1.0 * initial / runtime;

  printf("trying %d iterations\n", iterations);

  runtime = runit(iterations);

  printf("%d iterations take %7.5f seconds\n", iterations, runtime);

  runtime = runtime / (1.0 * iterations);

  printf("one iteration takes %12.10f seconds\n", runtime);

  printf("one iteration takes %12.3f  nsec\n", runtime * 1000000000.0);


  int b = sizeof(struct entry);
  printf("size of struct is %d\n", b);

  return(0);

}
