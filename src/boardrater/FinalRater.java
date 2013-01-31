package boardrater;
import tetris.Board;

/*
 * Default Genetic with all the boardraters but no weights.
 */

//I don't think the genetic algorithm will really have this form.
//The genetic algorithm will be in the form of a method that runs
//for a LONG, LONG time and then returns a list of coefficients.
//It's not actually a rater itself... just a coefficient finder.
//So, I don't know how long-lived this file will be.

public class FinalRater extends BoardRater {
 public static BoardRater raters[] = //staticness prevents these raters from getting instantiated over and over and over and over again... this'll save garbage collection time.
 {
    new ConsecHorzHoles(),
    new HeightAvg(),
    new HeightMax(),
    new HeightMinMax(),
    new HeightVar(),
    new HeightStdDev(),
    new SimpleHoles(),
    new ThreeVariance(),
    new Trough(),
    new WeightedHoles(),
    new RowsWithHolesInMostHoledColumn(),
    new AverageSquaredTroughHeight(),
    new BlocksAboveHoles(),
    new RowsWithHoles()
 };
 
//these weights are hand-tuned only.
//   public double[] coefficients = {
// /*new ConsecHorzHoles(),*/                0,  
// /*new HeightAvg(),*/                      10,
// /*new HeightMax(),*/                      1,
// /*new HeightMinMax(),*/                   1,
// /*new HeightVar(),*/                      0,
// /*new HeightStdDev(),*/                   5,
// /*new SimpleHoles(),*/                    40,
// /*new ThreeVariance(),*/                  10,
// /*new Trough(),*/                         1,
// /*new WeightedHoles(),*/                  4,
// /*new RowsWithHolesInMostHoledColumn()*/  4,
// /*new AverageSquaredTroughHeight()*/      15,
// /*new BlocksAboveHoles()*/                2,
// /*new RowsWithHoles()*/                   0
//   };


/***********DEFUALT WEIGHTS. UNCOMMENT THE DESIRED WEIGHTS.**************/

//these are weights from gen. 21 of the genetic algorithm, starting with the hand tuned weights above.
//these weights have scored 1.43 million in a single ply game.
 //public double[] coefficients = {0.07996672957203162, 5.249915291143696, 0.7615980333336664, 1.5793630193033281, -0.05020715891195912, 2.3439280170167276, 28.823943116495848, 12.357422820878064, 1.2165324765507346, 2.7357785144348763, 4.635003883701018, 24.02658382296249, 0.3853758982469925, 0};

//these weights are from gen. 35 of the genetic algorithm, starting with a list of all 1s for weights.
//these weights have scored 1.69 million in a single ply game.
 //public double[] coefficients = {2.49410038938842, 0.9388492572347871, -0.48140765187055645, 0.32990060828351453, 0.5574379956647663, 0.3538547940221663, 1.7495740291407684, 0.8704507143191742, 1.2571443652090235, 0.8965065762788876, 2.2880437169435592, 3.5804223716291204, -0.15808178736719514, 0};
 
//these weights are from gen. 70 of the genetic algorithm, starting with a list of all zeroes for weights.
//these weights have scored 2.5 million in a single ply game. other weights have obtained 2.8 million, but these averaged the highest.
 //public double[] coefficients = {0.9686097026795061, 0.1366862371509124, 0.1959640814385032, -0.4157005367058263, 0.4393275614613794, -0.19272725359581952, 0.643303423041282, 0.3048018715760217, 0.5230983454901121, 0.406929661228957, 0.2525305054989866, 1.4247599835416362, 0.0286589312318309, 0};
 
//these weights are also from gen. 70 of the genetic algorithm, starting with zeroes for all weights.
//these weights have obtained 2.8 million, but averaged less than the above weights over the 5 seeded games.
 //public double[] coefficients = {0.8791266314811436, 0.12573951551431792, 0.24997483966927936, -0.37622392923737297, 0.3691940742930254, -0.15248712080838983, 0.47572218993649196, 0.5080173951623478, -0.17424868949023015, 0.3063003516304143, 0.20505056555309084, 1.2349924733759654, -0.004861089935609013, 0};

//these weights are from gen. 64 of a second run of the genetic algorithm, starting with zeroes for all weights.
//these weights have obtained 5.23 million, and averaged 2.34 million over the five seeded games. This is on par with Jamie Dellacherie's 2003 one-piece algorithm, which averages 1625000 pieces over many games! Must test these weights in one-ply games over many many seeds to see if these weights beat his algorithm!!
//UPDATE: Ran these weights for 38 games in SingleBrainTest, and they averaged 2.3 million pieces over all 38 games, with the longest game reaching 12,689,477 pieces.
 //public double[] coefficients = {0.41430724103382527, 0.04413383739389207, 0.1420172532064692, -0.13881428312611474, 0.22970827267905328, -0.052368130931930074, 0.5712789822642919, 0.2851778629665227, 0.041534211381371554, -0.011738293785449829, 0.241299661945633, 0.8292064267563932, -0.009937763420971586, 0};

//these weights are from gen. 89 of that same second run of the genetic algorithm, starting with zeroes for all weights.
//In the genetic algorithm, these weights obtained 8,458,533 pieces in their longest game, and averaged 3,440,365 pieces over all five games.
 //public double[] coefficients = {0.3873903606334963, -0.04585633721581077, 0.09445581478240499, -0.07473365052873632, 0.07482815637667657, -0.11868667604426739, 0.4262318925468999, 0.3515108914987468, 0.34084080219176627, -0.09047581309278363, 0.27597446034724027, 0.8614281749606392, 0.03335335871335531, 0};

//these weights are from gen. 96 of that same second run of the genetic algorithm, starting with zeroes for all weights.
//In the genetic algorithm, these weights obtained 19,361,344 pieces in their longest game, and averaged 5,861,089.2 pieces over all five games.
 public double[] coefficients = {0.41430724103382527, 0.04413383739389207, 0.1420172532064692, -0.13881428312611474, 0.06887679285238696, -0.052368130931930074, 0.33235754477242435, 0.2851778629665227, -0.03011693088344261, -0.02534983335709433, 0.21155050264421074, 0.8292064267563932, 0.0038145282373974604, 0};

//these weights are obtained from testing a reckless 2-ply brain in the genetic algorithm for 100 generations ON A 6 BY 12 BOARD. 
//these may or may not scale up to a 10x20 very well ;-)
 //public double[] coefficients = {0.21695764825310368, 0.015484314708510895, -0.08512557714786578, -0.08405969708829672, 0.059656764700398955, 0.1332411646741821, 0.47178903037027686, 0.1119740846941119, -0.35049715878251264, 0.7187357441747413, 0.11497732766424408, 0.8499384885167329, -0.007611642913445417, 0};
 /***********END DEFAULT WEIGHTS. UNCOMMENT THE DESIRED WEIGHTS.**********/
 
 
 public FinalRater() {
   // System.out.println("new final rater:");
   // String temp;`
   // for(int i=0; i<raters.length; i++) {
   //   System.out.println((temp=""+coefficients[i]).substring(0,temp.length()>=4?temp.length():3)+"\t\t"+raters[i]);
   // }
//   for(int i=0; i<this.coefficients.length; i++)       //UNCOMMENT THIS LOOP TO NEGATE THE WEIGHTS AND SEE HOW BADLY IT KNOWS HOW TO PLAY!
//     this.coefficients[i] = 0-this.coefficients[i];
 }
 
 public FinalRater(double[] c) {
   if(c.length!=FinalRater.raters.length) {
     System.out.println("Make sure that the array passed into the FinalRater has the correct number of coefficients! Using DEFAULT COEFFICIENTS instead!");
     return;
   }
   this.coefficients = c;
//   for(int i=0; i<this.coefficients.length; i++)       //UNCOMMENT THIS LOOP TO NEGATE THE WEIGHTS AND SEE HOW BADLY IT KNOWS HOW TO PLAY!
//     this.coefficients[i] = 0-this.coefficients[i];
 }
 
 double rate(Board board) {
   double score = 0, temp;
   for (int x=0; x<raters.length; x++) {
     score += (temp=this.coefficients[x])==0?0:temp*FinalRater.raters[x].rate(board);
     // System.out.print(this.coefficients[x]);
   }
   return score;
 }
 
 double rate(Board board, double[] coefficients) {
   double[] temp = this.coefficients;
   this.coefficients = coefficients;
   double ret = this.rate(board);
   this.coefficients = temp;
   return ret;
 }
}
