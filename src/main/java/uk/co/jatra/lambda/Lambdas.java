package uk.co.jatra.lambda;

import rx.Observable;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

public class Lambdas {

    /*
    When Java started out, you didnt need to be a computer scientist to understand it. Admittedly you had to understand
    object oriented programming, but there are so many metaphors that relate to real world cases that it only takes
    a little thought to see whats going on. (The Animal metaphors or the Vehicle metaphors have been used many times.)

    Compared to other languages (eg C++) Java code was clean and readable. Virtually no weird operators or syntatical
    structures. The ternary operator was about the limits of readability stress. SOme people found references a bit
    tricky to understand - but that was mostly due to jumping to conclusions rather than thinking about things.

    However, the relative simplicity of the language had it's drawbacks, and certainly frustrated serious programmers.

    Since applets were a key original focus, event handling was obviously important, but since applets are likely to
    be small functional elements, the events were typically handled by the applet itself: no need to add listeners
    to UI components. This works since raw events are passed up through the containment hierarchy until something handles
    them. (http://math.hws.edu/eck/cs124/javanotes1/c6/s3.html)
    Since you were extending Applet anyway, that was a good place to override the event handling methods.
    The downside is that as an applet becomes more complex, there's little separation of code, and the applets class
    becomes bigger and bigger.

    Java 1.1 introduced inner classes,


    What IS a Java lambda?
    In use, it's a shorthand for an instance of an anonymous class that implements a functional interface.

    "Conceptually, a functional interface has exactly one abstract method."

    For example, Runnable is a functional Interface.

    Since functional interfaces have just one method, the name of the method and the type of the arguments can be
    implied. If an instance of that type is created, the implemented method
    does not have to be defined by name, nor the type of any arguments. All that is required is to specify any argument
    variable names, and the code to execute on them.

    The simplest example is where there is a single function that tkes no argments, such as in Runnable. Here, there
    is no need to specify the name of the method (run).
    */

    private Runnable myRunnable = () -> System.out.println("Hello world!");

    /*
    It's also possible to use a lambda where a return value is expected.
    For example, a PathMatcher which expects a Path as an argument, returns a boolean.
    Creating using an anonymous class could be:
    */
    private PathMatcher matcherAnon = new PathMatcher() {
        @Override
        public boolean matches(Path path) {
            return path.getNameCount() == 1;
        }
    };

    /*
    Since PathMatcher is a functional interface, the implementation can be a lambda:
     */

    private PathMatcher matcher = (path) -> {
        return path.getNameCount() == 1;
    };

    /*
    When there is only one argument, the parantheses are not required, and conventionally a single letter argument name
    is used
     */

    private PathMatcher matcher2 = p -> {
        return p.getNameCount() == 1;
    };

    /*
    If the lamdba body is just the return value, then no need for the braces, nor the return statement.
     */
    private PathMatcher matcher3 = p -> p.getNameCount() == 1;

    /*
    If multiple arguments are used, then parentheses are required.
     */
    private Comparator<String> hashComparator = (a, b) -> a.hashCode() - b.hashCode();


    /*
    The Java8 java.util.function package has a set of functional interfaces describing likely uses.
     */

    private Consumer<String> consumer = s -> System.out.println(s); //consumes an argument, produces no return.

    private Function<String, Integer> strlen = s -> s.length(); //applies a function

    private UnaryOperator<String> lowerCase = s -> s.toLowerCase(); //applies a function that returns same type

    private Supplier<String> supplier = () -> "Hello world!"; //takes no arguments, supplies a return value

    private Predicate<Integer> even = i -> i % 2 == 0; //returns a boolean result of a test on the argument.

    /*
    Because lambdas are shorthand for instances of anonymous classes, they are objects, so can be
    passed as arguments to methods.
    Thus if you have a method that takes a Functional Interface, you can use a lambda.
    For example, when creating a Thread, you can give it a Runnable:
     */

    private Thread operation = new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello!");
        }
    });

    /*
    Using a lambda
     */
    private Thread operation2 = new Thread(() -> System.out.println("Hello!"));

    /*
    Just like any other anonymous class, a lambda does not need to be assigned to a variable
     */

    private void doStuff() {
        new Thread(() -> System.out.println("Hello!"))
                .start();
    }

    /*
    When used as event handlers, lambdas can really simplify code:
     */

    private void addListenerToButton(Button button) {
        button.addActionListener(v -> System.out.println("Button Pressed"));
    }

    /*
    Or for Android:

    private void addListenerToButton(Button button) {
        button.setOnClickListener(v -> System.out.println("Button Pressed"));
    }
    */


    /*
    A typical use of lambdas is to apply some method to transform or process each element in a collection.
     */

    private <T> int count(Collection<T> collection, Predicate<T> predicate) {
        int count = 0;
        for (T item : collection) {
            if (predicate.test(item)) count++;
        }
        return count;
    }

    private void somewhereElse() {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,23,45,855,23332);
        int numberOfOdds = count(numbers, i -> i%2 == 1);
    }

    /*
    A number of libraries implement this idea, such as Java8 Streams, or RxJava.

    Streams have a map() method, that applies a Function to each element, and returns a stream of the results.
    */

    Stream<String> stringStream = Stream.of("hello", "hi", "hallo", "hullo", "goodbye");

    Stream<Integer> lengthStream = stringStream.map(s -> s.length());

    /*
    Similarly, Rx Observables also have a map method.
     */
    Observable<String> stringObservable = Observable.just("hello", "hi", "hallo", "hullo", "goodbye");

    Observable<Integer> lengthsObservable = stringObservable.map(s -> s.length());

    /*
    Libraries such as Streams or Observables are where lambdas can make the code a lot less verbose.
    As can be seen above, Streams and Observables are fluent apis: the methods returns the type they operate on.
    This means you can chain methods:
     */
    int totalLength = stringStream
            .map(s -> s.length())
            .reduce((a,b) ->a+b)
            .get();


    private void method(Stream<String> stringStream) {
        stringStream.filter(s -> s.length() > 2);
    }

    private boolean test(int i, Predicate<Integer> predicate) {
        return predicate.test(i);
    }

    private void oddsAndEvens() {
        for (int i=0; i<10; i++) {
            System.out.println(i + (test(i, even) ? " is even" : " is odd"));
        }
    }

    private abstract static class FunctionalAbstractClass {
        abstract int onlyMethod(int a, int b);
    }

    @FunctionalInterface  //optional.
    private static interface FunctionalInterf {
        int onlyMethod(int a, int b);
    }

    public static void main(String[] args) {
        FunctionalInterf funcIf = new FunctionalInterf() {
            @Override
            public int onlyMethod(int a, int b) {
                return a + b;
            }
        };

        for (int i=0; i<4; i++) {
            System.out.println(i + "+1 = " + funcIf.onlyMethod(i, 1));
        }

        FunctionalInterf funcIf2 = (a, b) -> { return a+b; };
        for (int i=0; i<4; i++) {
            System.out.println(i + "+1 = " + funcIf2.onlyMethod(i, 1));
        }


        BiFunction<Integer, Integer, Integer> biFunction = (a, b) -> {return a+b; };
        for (int i=0; i<4; i++) {
            System.out.println(i + "+1 = " + biFunction.apply(i, 1));
        }


        //Cannot create lambda from this one.
        FunctionalAbstractClass fac = new FunctionalAbstractClass() {
            @Override
            int onlyMethod(int a, int b) {
                return a+b;
            }
        };
        for (int i=0; i<4; i++) {
            System.out.println(i + "+1 = " + fac.onlyMethod(i, 1));
        }

        IntPredicate isOdd = i -> i%2 != 0; //has a single method "test"
        for (int i=0; i<10; i++) {
            System.out.println(i+" is odd " + isOdd.test(i));
        }
    }


}
