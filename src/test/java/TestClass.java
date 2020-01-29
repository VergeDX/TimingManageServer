public class TestClass {
    public static void main(String[] args) {
        System.out.println(new B().a());
        System.out.println(new B().a() == B.class);
    }

    public static abstract class A {
        public Class<? extends A> a() {
            return this.getClass();
        }
    }

    public static class B extends A {

    }
}
