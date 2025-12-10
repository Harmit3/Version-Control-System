public class Counternew {

    private int count;

    public void increment() {
        count++;
    }

    public void decrement() {    
        count--;
    }

    public void reset() {
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
        Counternew counter = new Counternew();  
        counter.increment();
        counter.increment();
        counter.decrement();              
        System.out.println("Count: " + counter.getCount());  
        counter.reset();
        System.out.println("After reset: " + counter.getCount()); 
    }
}
