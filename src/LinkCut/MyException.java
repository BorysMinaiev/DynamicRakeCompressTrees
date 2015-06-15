package LinkCut;

/**
 * author: Danny Sleator
 * based on http://codeforces.com/contest/117/submission/860934
 */
class MyException extends AssertionError {
    public String s;
    public MyException(String prob) {
        super(prob);
        s = prob;
    }
}