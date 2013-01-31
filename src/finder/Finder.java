package finder;
import boardrater.*;
import tetris.*;
import java.util.Random;
import java.util.Date;
//For simplicity, I'm limiting the range of weights to between zero and ten.
//They're all doubles anyway, so it shouldn't matter what the actual upper bound is.
//It just can't be infinitely variable, because there'd be an infinite number of
//children that could lead the same game. (i.e. with proportional weights)

//Kids are lists of doubles (lists of weights).
//Fitnesses are ints, and higher is better.

public class Finder {  
  public static void main(String[] args) {
    Finder f = new Finder();
    double[] temp,temp2;
    // pa((temp=f.newRandomKidFromBaseline()));
    // pa(temp2=f.mutateKid(f.dupeKid(temp)));
    // p(f.equals(temp,temp2));
    // p(temp[0]);
    // p(temp2[0]);
    // p(f.fitnessOf(f.newRandomKidFromBaseline()));
    f.go();
  }
  
  FinalRater rater = new FinalRater();                    //This is used solely by the fitnessOf method to simulate a game with a given kid. (Each kid is a list of weights, remember.)
  Brain raterUser;                                        //This is used solely by the fitnessOf method to simulate a game with the rater.
  TetrisController controller = new TetrisController();   //This is used solely by the fitnessOf method to simulate a game with the brain.
  static int NUM_KIDS   = 10;                    //This is the population size.
  static int numWeights = -1;                    //This is the number of weights, derived straight from the FinalRater class in the boardrater package.
  double[][] pop;                         //This is the actual population - the list of lists of weights.
  double[][] nextPop;
  int[] fitnesses;                        //This is the list of fitnesses obtained after each generation, used to sort.
  Random randy = new Random();            //random generator
  int genCount = 0;
  
  public Finder() {
    this.numWeights = FinalRater.raters.length;
    this.pop = new double[this.NUM_KIDS][this.numWeights];
    this.nextPop = new double[this.NUM_KIDS][this.numWeights];
    this.fitnesses = new int[this.NUM_KIDS];
    this.raterUser = new Ply1Brain();
    this.raterUser.setRater(this.rater);
  }
  
  void go() {
    for(int i=0; i<NUM_KIDS; i++)
      pop[i] = newRandomKid();
    nextGen();
  }
  //012012345
  void nextGen() {                            //This is the method called to produce the next generation of weight list kids.
    p("\nGENERATION "+genCount);
    pa(pop);
    p("");
    patrue(pop);
    genCount++;
    int count = 0;                            //It works by starting with the most fit and using appropriate mating/mutation methods to
    for(int i=0; i<NUM_KIDS; i++) {           //gradually fill up the nextPop array with the new children.
      if(i<NUM_KIDS*0.3) {                               //afterwards, it swaps nextPop with pop, gets the fitnesses of the new population, and then sorts.
        nextPop[count] = dupeKid(pop[i],fitnesses[i]);              //After this method, the population is in the same state it was in before this method was called.
        count++;
      }
      if(i<NUM_KIDS*0.4 && count<NUM_KIDS) {
        nextPop[count] = mutateKid(mateKids(pop[i],pop[i+1]));
        count++;
      } else if(i<NUM_KIDS*0.9 && count<NUM_KIDS) {
        nextPop[count] = mutateKid(dupeKid(pop[i]));
        count++;
      }
      else if(i>=NUM_KIDS*0.9 && count<NUM_KIDS) {
        nextPop[count] = newRandomKid();
        count++;
      }
    }
    for(int i=0; i<NUM_KIDS; i++) {                                             //for every kid...
      for(int j=i+1; j<NUM_KIDS; j++) {                                           //for all the rest of the kids...
        if(equals(nextPop[i],nextPop[j])) {                                         //if the two kids are the same...
          if(nextPop[j].length == numWeights + 1 && nextPop[i].length == numWeights)   //if the one that's gonna die has a precomputed fitness on it, make sure to save it.
            nextPop[i] = nextPop[j];
          nextPop[j] = newRandomKid();                                                //now rid the dupe!
        }
      }
    }
    double[][] temp;                          //now that nextPop is full of new children, swap it with pop so that pop becomes the drawing board for the NEXT generation.
    temp = pop;
    pop = nextPop;
    nextPop = temp;
    getFitnesses();                          //figure out what all the fitnesses of the children are.
    quicksortByFitness();                    //sort the new population, to put the most fit children in the front of the list for the next generation.
    nextGen();
  }
  
  double[] newRandomKid() {                  //This method generates a new random child. Each child is a list of weights.
    double[] kid = new double[numWeights];
    for(int i=0; i<numWeights; i++)
      kid[i] = randy.nextDouble()*1.0-0.5; //Each new random kid will have all weights between 0.5 and 1.5.
    return kid;
  }
  
  double[] newRandomKidFromBaseline() {      //This method generates a new random child from a baseline list of hand-selected weights.
    double[] baseline = {
/*new ConsecHorzHoles(),*/                0,  
/*new HeightAvg(),*/                      10,
/*new HeightMax(),*/                      1,
/*new HeightMinMax(),*/                   1,
/*new HeightVar(),*/                      0,
/*new HeightStdDev(),*/                   5,
/*new SimpleHoles(),*/                    40,
/*new ThreeVariance(),*/                  10,
/*new Trough(),*/                         1,
/*new WeightedHoles(),*/                  4,
/*new RowsWithHolesInMostHoledColumn()*/  4,
/*new AverageSquaredTroughHeight()*/      15,
/*new BlocksAboveHoles()*/                2
    };
    double[] kid = new double[numWeights];
    double temp;
    for(int i=0; i<numWeights; i++)
      kid[i] = (temp=baseline[i])+(randy.nextDouble()*0.2-.1)*temp; //Each new random kid will have all weights within 10 percent of the baseline weights.
    return kid;
  }
  
  double[] mutateKid(double[] kid) {      //This slightly tweaks all of the parameters in the kid. DOES NOT CREATE A COPY.
    for(int i=0; i<numWeights; i++) {
      if(randy.nextDouble()>0.5) {
        kid[i] *= 1+(randy.nextDouble()*0.3-0.15);  //Add a random number between -0.15 and 0.15 to each value in the kid.
        kid[i] += randy.nextDouble()*0.3-0.15;
      }
    }
    return kid;
  }
  
  double[] tweakKid(double[] kid) {       //This slightly tweaks ONE of the parameters in the kid. DOES NOT CREATE A COPY.
    int i = randy.nextInt(numWeights);
    kid[i] += randy.nextDouble()*0.5-0.25;  //Add a random number between -1/4 and 1/4 to one random value in the kid.
    return kid;
  }
  
  double[] dupeKid(double[] kid) {        //This does nothing more than create a duplicate of the kid passed to it.
    double[] dupe = new double[numWeights];
    for(int i=0; i<numWeights; i++) dupe[i] = kid[i];
    return dupe;
  }
  
  double[] dupeKid(double[] kid, int fitness) { //This dupes a child and puts the fitness as the N+1st element, to avoid future calculation.
    double[] dupe = new double[numWeights+1];
    for(int i=0; i<numWeights; i++) dupe[i] = kid[i];
    dupe[numWeights] = fitness;
    return dupe;
  }
  
  double[] mateKids(double[] one, double[] two) {   //This mates two kids and produces offspring: the offspring is a new object.
    double[] kid = new double[numWeights];
    for(int i=0; i<numWeights; i++)
      kid[i] = randy.nextBoolean()?one[i]:two[i];
    return kid;
  }
  
  boolean equals(double[] one, double[] two) {
    for(int i=0; i<numWeights; i++)
      if(one[i]!=two[i]) return false;
    return true;
  }
  
  void getFitnesses() {
    for(int i=0; i<NUM_KIDS; i++) {
      fitnesses[i] = fitnessOf(pop[i]);
    }
  }
  
  void quicksortByFitness() {
    quicksortByFitness(0,NUM_KIDS-1);
    double[] temp;
    int tempfit;
    for(int i=0,m=NUM_KIDS/2; i<m; i++) {
      temp = pop[i];
      pop[i] = pop[NUM_KIDS-1-i];
      pop[NUM_KIDS-1-i] = temp;
      tempfit = fitnesses[i];
      fitnesses[i] = fitnesses[NUM_KIDS-i-1];
      fitnesses[NUM_KIDS-1-i] = tempfit;
    }
  }
  
  void quicksortByFitness(int startlo, int starthi) {   //This is a quicksort that swaps the children in the gen array right along with the fitnesses in the fitnesses array. It just keeps them aligned.
    if(startlo>=starthi) return;              //get out if the current list to sort is length zero or less...
    int lo = startlo;
    int hi = starthi;
    if(hi-1==lo) {                            //handle the case where we swap only a two-element list
      if(fitnesses[lo]>fitnesses[hi]) {
        int temp = fitnesses[lo];             //swap!
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        double[] tempkid = pop[lo];          //also swap the kids themselves... not only the fitnesses.
        pop[lo] = pop[hi];
        pop[hi] = tempkid;
      }
      return;                                 //get out if we just finished swapping the only two things in the list.
    }
    int piv = fitnesses[(lo+hi)/2];           //choose pivot
    fitnesses[(lo+hi)/2] = fitnesses[hi];     //...and swap it away for now
    fitnesses[hi] = piv;
    double[] Kidpivot = pop[(lo+hi)/2];      //now swap the kids to stay aligned!
    pop[(lo+hi)/2] = pop[hi];
    pop[hi] = Kidpivot;
    while(lo<hi) {
      while(fitnesses[lo]<=piv && lo<hi)      //go up from lo till bigger is found... 
        lo++;
      while(piv<=fitnesses[hi] && lo<hi)      //go down from hi till smaller is found...
        hi--;
      if(lo<hi) {                             //swap lo and hi if out of order
        int temp = fitnesses[lo];
        fitnesses[lo] = fitnesses[hi];
        fitnesses[hi] = temp;
        double[] tempkid = pop[lo];          //now swap the kids!
        pop[lo] = pop[hi];
        pop[hi] = tempkid;
      }
    }
    fitnesses[starthi] = fitnesses[hi];       //put pivot back in the middle...
    fitnesses[hi] = piv;
    pop[starthi] = pop[hi];
    pop[hi] = Kidpivot;
    quicksortByFitness(startlo,lo-1);       //Recurse now that we're sorted around the pivot.
    quicksortByFitness(hi+1,starthi);
  }
  
  int fitnessOf(double[] kid) {
    if(kid.length == numWeights+1) {
      System.out.println("Precalculated Average Score: " + (((int)(kid[numWeights]))/5.0));
      return (int) kid[numWeights];
    }
    int totalScore = 0;
    for(int seed = 0; seed<5; seed++)
      totalScore+=playGame(kid, seed);
    System.out.println("Average Score: " + totalScore/5.0);
    return totalScore;
  }
  
  int playGame(double[] kid, int seed) {
    //todo make a tetris controller and use it to evaluate a kid in this method.
    //do this by setting the coefficients of the FinalRater to the kid, and giving the
    //FinalRater to the TetrisController so the brain can use it to play the game.
		TetrisController tc = controller;
    rater.coefficients = kid;
		tc.startGame(seed);

		Date start = new Date();

    long lastDisplay = System.currentTimeMillis(),tempTime;
    boolean displayed = false;
		while (tc.gameOn) {
			Move move = raterUser.bestMove(new Board(tc.board), tc.currentMove.piece, tc.nextPiece, tc.board.getHeight() - TetrisController.TOP_SPACE);
			while (!tc.currentMove.piece.equals(move.piece)) tc.tick(TetrisController.ROTATE);
			while (tc.currentMove.x != move.x) tc.tick(((tc.currentMove.x < move.x) ? TetrisController.RIGHT : TetrisController.LEFT));
			int current_count = tc.count;
			while ((current_count == tc.count) && tc.gameOn) tc.tick(TetrisController.DOWN);
      if((tempTime=System.currentTimeMillis()) - lastDisplay > 10000) {
        if(!displayed) {
          displayed = true;
          System.out.print((seed==0?"\n":"")+".");
        }
        lastDisplay = tempTime;
        System.out.print(".."+tc.count+".");
      }
		}
		if(!displayed) {
      System.out.print((seed==0?"\n":"")+".");
		}
		p(".."+tc.count+".");
		return tc.count;
  }
  
  
  
  static void p() {              //This is just handy.
    System.out.println();
  }
  static void p(Object o) {              //This is just handy.
    System.out.println(o);
  }
  static void pa(double[] x) {              //This is just handy.
    System.out.print('[');
    for(int i=0; i<x.length; i++) {
      System.out.print(((int)(x[i]*100))/100.0);
      if(i<x.length-1) System.out.print(",\t");
    }
    p("]");
  }
  static void pa(double[][] x) {              //This is just handy.
    System.out.print("[");
    for(int i=0; i<x.length; i++) {
      pa(x[i]);
      if(i<x.length-1) System.out.print(" ");
    }
    p("]");
  }
  
  
  static void patrue(double[] x) {              //This is just handy.
    System.out.print('[');
    for(int i=0; i<x.length; i++) {
      System.out.print(x[i]);
      if(i<x.length-1) System.out.print(", ");
    }
    p("]");
  }
  static void patrue(double[][] x) {              //This is just handy.
    System.out.print("[");
    for(int i=0; i<x.length; i++) {
      patrue(x[i]);
      if(i<x.length-1) System.out.print(" ");
    }
    p("]");
  }
  
}

//todo make mutations of kids with higher fitnesses change their values by a lesser amount each time.