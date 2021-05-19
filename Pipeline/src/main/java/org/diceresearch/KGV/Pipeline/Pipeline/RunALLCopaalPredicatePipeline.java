package org.diceresearch.KGV.Pipeline.Pipeline;

public class RunALLCopaalPredicatePipeline {
    public static void main(String[] args) throws Exception {
        Run();
    }

    public static String[] predicates  = {"writer","leader","mainInterests","exports","owner","spouse","place","birthPlace","musicComposer","live","deathPlace"};

    public static void Run() throws Exception {
        for(String p:predicates){

                //RunCopaalPipeline.Run(p,2,false);
                RunCopaalPipeline.Run(p,2,true);
                //RunCopaalPipeline.Run(p,3,false);
                //RunCopaalPipeline.Run(p,3,true);

        }

    }
}
