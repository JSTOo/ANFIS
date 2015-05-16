package trash;

import rules.Conclusion;
import rules.ConclusionSetFabric;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Created by Nixy on 10.04.2015.
 */
public class RandomConclusionSet implements ConclusionSetFabric {
    @Override
    public Deque<Conclusion> makeSet(List<Conclusion> conclusions, int tail) {
        Deque<Conclusion> result = new  ArrayDeque<Conclusion>();
        result.add(conclusions.remove(conclusions.size() - 1));
        int size = conclusions.size();
        Random r = new Random();
        for (int i = 0; i < size && i < tail-1; i++) {
            int p = r.nextInt(conclusions.size());
            result.add(conclusions.remove(p));

        }
        return result;
    }
}
