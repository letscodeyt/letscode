package net.letscode.game.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;

/**
 * A quick and dirty benchmark to compare basic list operations between types.
 * As it turns out, LinkedHashSet is really awesome.
 * @author timothyb89
 */
public class SetBench extends Object {
	
	public static final int ELEMENTS = 1000;
	
	private Collection<Integer> collection;
	
	public SetBench(Collection<Integer> collection) {
		this.collection = collection;
		
		System.out.println("Testing: " + collection.getClass().getSimpleName());
		AdditionTest add = new AdditionTest();
		add.run();
		System.out.printf("Addition took %.2f ms (%f ms / element)\n",
				add.time, add.time / collection.size());
		
		IterationTest iter = new IterationTest();
		iter.run();
		System.out.printf("Iteration took %.2f ms (%f ms / element)\n",
				iter.time, iter.time / collection.size());
		
		LookupTest lookup = new LookupTest();
		lookup.run();
		System.out.printf("Lookup took %.2f ms (%f ms / element)\n", 
				lookup.time, lookup.time / collection.size());
		
		System.out.println();
	}
	
	public abstract class TimedTask implements Runnable {

		long startTime;
		long endTime;
		double time;
		double seconds;
		
		@Override
		public void run() {
			startTime = System.currentTimeMillis();
			execute();
			endTime = System.currentTimeMillis();
			
			time = endTime - startTime;
			seconds = time / 1000d;
		}
		
		public abstract void execute();

		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + seconds;
		}
		
	}
	
	public class AdditionTest extends TimedTask {

		@Override
		public void execute() {
			Random r = new Random();
			
			while (collection.size() < ELEMENTS) {
				collection.add(r.nextInt(ELEMENTS * 10));
			}
		}
		
	}
	
	public class IterationTest extends TimedTask {

		int sum;
		
		@Override
		public void execute() {
			sum = 0;
			
			Iterator<Integer> iter = collection.iterator();
			while (iter.hasNext()) {
				sum += iter.next();
			}
		}
		
	}
	
	public class LookupTest extends TimedTask {

		int found;
		
		@Override
		public void execute() {
			Random r = new Random();
			
			found = 0;
			for (int i = 0; i < ELEMENTS * 10; i++) {
				if (collection.contains(r.nextInt(ELEMENTS))) {
					found++;
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("No initial capacity");
		System.out.println("===================");
		new SetBench(new ArrayList<Integer>());
		new SetBench(new LinkedList<Integer>());
		new SetBench(new HashSet<Integer>());
		new SetBench(new LinkedHashSet<Integer>());
		
		System.out.println("Initial capacity of 100000");
		System.out.println("==========================");
		new SetBench(new ArrayList<Integer>(100000));
		new SetBench(new LinkedList<Integer>());
		new SetBench(new HashSet<Integer>(100000));
		new SetBench(new LinkedHashSet<Integer>(100000));
	}
	
}
