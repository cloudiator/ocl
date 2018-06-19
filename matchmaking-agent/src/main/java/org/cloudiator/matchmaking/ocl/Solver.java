package org.cloudiator.matchmaking.ocl;

import cloudiator.CloudiatorFactory;
import cloudiator.CloudiatorPackage;
import com.google.inject.Inject;
import javax.annotation.Nullable;
import org.cloudiator.matchmaking.ocl.NodeCandidate.NodeCandidateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solver {

  private static final CloudiatorFactory cloudiatorFactory = CloudiatorPackage.eINSTANCE
      .getCloudiatorFactory();
  private static final NodeCandidateFactory nodeCandidateFactory = NodeCandidateFactory.create();
  private static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);

  private final ModelGenerator modelGenerator;

  @Inject
  public Solver(ModelGenerator modelGenerator) {
    this.modelGenerator = modelGenerator;
  }

  @Nullable
  public Solution solve(OclCsp csp, String userId) throws ModelGenerationException {
    ConstraintChecker cc = ConstraintChecker.create(csp);

    LOGGER.debug(String.format("%s is solving CSP %s for user %s", this, csp, userId));

    NodeGenerator nodeGenerator =
        new ConsistentNodeGenerator(
            new DefaultNodeGenerator(nodeCandidateFactory, modelGenerator.generateModel(userId)),
            cc);
    SolutionGenerator solutionGenerator = new SolutionGenerator(nodeGenerator);
    long startGeneration = System.currentTimeMillis();

    LOGGER.debug(String.format("CSP %s has %s initial solutions.", csp,
        solutionGenerator.generateInitialSolutions().size()));

    long generationTime = System.currentTimeMillis() - startGeneration;
    System.out.println(
        String.format("Possible Solution Generation for CSP %s took %s", csp, generationTime));

    BestFit bestFit = new BestFit(solutionGenerator, cc, 100,
        1);

    long startSolving = System.currentTimeMillis();

    Solution solution = bestFit.solve();

    if (solution.noSolution()) {
      return null;
    }

    long solvingTime = System.currentTimeMillis() - startSolving;
    solution.setTime(solvingTime);

    LOGGER.debug(String
        .format("%s found a solution %s for CSP %s in %s milliseconds", this, solution, csp,
            solvingTime));

    return solution;
  }

}