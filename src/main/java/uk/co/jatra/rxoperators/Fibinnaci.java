package uk.co.jatra.rxoperators;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by tim on 01/05/2016.
 */
public class Fibinnaci {
    private static Observable<Integer> counter = Observable.range(1, 10);

    public static void main(String[] args) {
        counter.subscribe(System.out::println);

        counter
                .map(new Func1<Integer, Object>() {
                    private int previous = 1;
                    private int penutimate = 0;
                    @Override
                    public Object call(Integer integer) {
                        int result = penutimate + previous;
                        penutimate = previous;
                        previous = result;
                        return result;
                    }
                })
                .subscribe(System.out::println);

        counter
                .lift(new Observable.Operator<Object, Integer>() {
                    @Override
                    public Subscriber<? super Integer> call(Subscriber<? super Object> subscriber) {
                        return null;
                    }
                });
    }
}
