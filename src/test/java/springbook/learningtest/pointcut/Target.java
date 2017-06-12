package springbook.learningtest.pointcut;

/**
 * @author Kj Nam
 * @since 2017-06-11
 */
public class Target implements TargetInterface {
    @Override
    public void hello() {
    }

    @Override
    public void hello(String a) {
    }

    @Override
    public int minus(int a, int b) throws RuntimeException {
        return 0;
    }

    @Override
    public int plus(int a, int b) {
        return 0;
    }

    public void method() {
    }
}
