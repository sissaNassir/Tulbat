package it.unibo.ai.didattica.competition.tablut.tulbat.MonteCarlo;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;
import aima.core.search.framework.Node;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.tulbat.framework.GameTree;
import it.unibo.ai.didattica.competition.tablut.tulbat.framework.NodeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Artificial Intelligence A Modern Approach (4th Edition): page ???.<br>
 *
 * <pre>
 * <code>
 * function MONTE-CARLO-TREE-SEARCH(state) returns an action
 *   tree &larr; NODE(state)
 *   while TIME-REMAINING() do
 *   	leaf &larr; SELECT(tree)
 *   	child &larr; EXPAND(leaf)
 *   	result &larr; SIMULATE(child)
 *   	BACKPROPAGATE(result, child)
 *   return the move in ACTIONS(state) whose node has highest number of playouts
 * </code>
 * </pre>
 *
 * Figure ?.? The Monte Carlo tree search algorithm. A game tree, tree, is initialized, and
 * then we repeat the cycle of SELECT / EXPAND / SIMULATE/ BACKPROPAGATE until we run  out
 * of time, and return the move that led to the node with the highest number of playouts.
 *
 *
 * @author Suyash Jain
 *
 * @param <S>
 *            Type which is used for states in the game.
 * @param <A>
 *            Type which is used for actions in the game.
 * @param <P>
 *            Type which is used for players in the game.
 */

public class MonteCarloTreeSearch<S, A, P> implements AdversarialSearch<S, A> {
    private Timer timer;
    private int iterations = 10;
    protected Game<S, A, P> game;
    protected GameTree<S, A> tree;

    public MonteCarloTreeSearch(Game<S, A, P> game, int iterations, int timeout) {

        this.game = game;
        this.iterations = iterations;
        tree = new GameTree<>();
        this.timer = new Timer(timeout);
    }


    @Override
    public A makeDecision(S state) {
        timer.start();
        // tree <-- NODE(state)
        tree.addRoot(state);
        // while TIME-REMAINING() do
        while ((!timer.timeOutOccurred())) {

            // leaf <-- SELECT(tree)
            Node<S, A> leaf = select(tree);
            // child <-- EXPAND(leaf)
            Node<S, A> child = expand(leaf);
            // result <-- SIMULATE(child)
            // result = true if player of root node wins
            boolean result = simulate(child);
            // BACKPROPAGATE(result, child)
            backpropagate(result, child);
            // repeat the four steps for set number of iterations
        }
        // return the move in ACTIONS(state) whose node has highest number of playouts
        return bestAction(tree.getRoot());
    }



    public Node<S, A> select(GameTree gameTree) {
        Node<S, A> node = gameTree.getRoot();
        while (!game.isTerminal(node.getState()) && isNodeFullyExpanded(node)) {
            node = gameTree.getChildWithMaxUCT(node);
        }
        return node;
    }

    public Node<S, A> expand(Node<S, A> leaf) {
        if (game.isTerminal(leaf.getState())) return leaf;
        else {
            Node<S, A> child = randomlySelectUnvisitedChild(leaf);
            return child;
        }
    }

    public boolean simulate(Node<S, A> node) {
        while (!game.isTerminal(node.getState())) {
            Random rand = new Random();
            A a = game.getActions(node.getState()).get(rand.nextInt(game.getActions(node.getState()).size()));
            S result = game.getResult(node.getState(), a);
            NodeFactory nodeFactory = new NodeFactory();
            node = nodeFactory.createNode(result);
        }
        if (game.getUtility(node.getState(), game.getPlayer(tree.getRoot().getState())) > 0) return true;
        else return false;
    }

    public void backpropagate(boolean result, Node<S, A> node) {
        tree.updateStats(result, node);
        if (tree.getParent(node) != null) backpropagate(result, tree.getParent(node));
    }

    public A bestAction(Node<S, A> root) {
        Node<S, A> bestChild = tree.getChildWithMaxPlayouts(root);
        S result = null;
        for (A a : game.getActions(root.getState())) {
            result = game.getResult(root.getState(), a);
            if (result.equals(bestChild.getState())) return a;
        }
    //    System.out.println("gno");
    //    System.out.println(bestChild.getState());
    //    System.out.println("gno");
    //    System.out.println(result);
    //    System.out.println("gno");
        return null;
    }

    public boolean isNodeFullyExpanded(Node<S, A> node) {
        List<S> visitedChildren = tree.getVisitedChildren(node);
        for (A a : game.getActions(node.getState())) {
            S result = game.getResult(node.getState(), a);
            if (!visitedChildren.contains(result)) {
                return false;
            }
        }
        return true;
    }


    private Node<S, A> randomlySelectUnvisitedChild(Node<S, A> node) {
        List<S> unvisitedChildren = new ArrayList<>();
        List<S> visitedChildren = tree.getVisitedChildren(node);
        for (A a : game.getActions(node.getState())) {
            S result = game.getResult(node.getState(), a);
            if (!visitedChildren.contains(result)) unvisitedChildren.add(result);
        }
        Random rand = new Random();
        Node<S, A> newChild = tree.addChild(node, unvisitedChildren.get(rand.nextInt(unvisitedChildren.size())));
        return newChild;
    }

    private static class Timer {
        private long duration;
        private long startTime;

        Timer(int maxSeconds) {
            this.duration = 1000 * maxSeconds;
        }
        void start() {
            startTime = System.currentTimeMillis();
        }

        boolean timeOutOccurred() {
            return System.currentTimeMillis() > startTime + duration;
        }
    }

    @Override
    public Metrics getMetrics() {
        return null;
    }

    //public abstract Action makeDecision(State.Turn state);
}