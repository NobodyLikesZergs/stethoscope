package edu.phystech.stethoscope.player;

import java.lang.Math;

public class Butterworth
{
  final double sampleRate = PlayerController.RATE;
  int N;
  
  double[] a, b, x, y;
  
  public void initialize(double w1, double w2)
  {
    double W1 = Math.tan(Math.PI * w1/sampleRate), W2 = Math.tan(Math.PI * w2/sampleRate);
    double Wc2 = W1 * W2;
    double B = W2 > W1 ? W2-W1 : W1-W2;

    double Rp = 3;
    double ep = Math.sqrt(Math.pow(10, 0.1*Rp) - 1);

    N = 3;
    int L = N/2;
    int r = N%2;

    // norm filter

    double alpha = Math.pow(ep, -1.0/N);

    Polinom polinom_bot = new Polinom();

    if (r == 1) {
      polinom_bot.setSize(2);
      polinom_bot.setValue(1, 1);
      polinom_bot.setValue(0, alpha);
    }
    else {
      polinom_bot.setSize(1);
      polinom_bot.setValue(0, 1);
    }

    for (int i=1; i<=L; i++) {
      double theta = Math.PI*(2*i-1)/(2*N);
      Polinom curr = new Polinom();
      curr.setSize(3);
      curr.setValue(2, 1);
      curr.setValue(1, 2 * alpha * Math.sin(theta));
      curr.setValue(0, alpha * alpha);

      polinom_bot = polinom_bot.multiply(curr);
    }

    // FNCH -> PF

    Polinom top = new Polinom();
    top.setSize(3);
    top.setValue(2, 1);
    top.setValue(0, Wc2);
    
    Polinom bot = new Polinom();
    bot.setSize(2);
    bot.setValue(1, B);

    Polinom polinom_top = new Polinom();
    polinom_top.setSize(1);
    polinom_top.setValue(0, 1.0/ep);
    for (int i=0; i<polinom_bot.size()-1; i++) {
      polinom_top = polinom_top.multiply(bot);
    }

    polinom_bot = polinom_bot.substitute(top, bot);

    // biliniar

    int mult = polinom_bot.size() - polinom_top.size();

    Polinom top2 = new Polinom();
    top2.setSize(2);
    top2.setValue(1, -1);
    top2.setValue(0, 1);

    Polinom bot2 = new Polinom();
    bot2.setSize(2);
    bot2.setValue(1, 1);
    bot2.setValue(0, 1);

    polinom_top = polinom_top.substitute(top2, bot2);
    polinom_bot = polinom_bot.substitute(top2, bot2);

    for (int i=0; i<mult; i++)
      polinom_top = polinom_top.multiply(bot2);

    // ok

    a = new double[polinom_top.size()];
    b = new double[polinom_bot.size()-1];

    x = new double[polinom_top.size()-1];
    y = new double[polinom_bot.size()-1];


    for (int i=0; i<polinom_top.size(); i++)
      a[i] = polinom_top.getValue(i)/polinom_bot.getValue(0);
    for (int i=0; i<polinom_bot.size()-1; i++)
      b[i] = polinom_bot.getValue(i+1)/polinom_bot.getValue(0);

    N = 2*N;
  }
  
  double process(double new_x)
  {
    if (N == 0)
        return 0;

    double new_y = a[0] * new_x;

    for (int i=0; i<N; i++)
        new_y += a[i+1] * x[i];
    for (int i=0; i<N; i++)
        new_y -= b[i] * y[i];

    for (int i=N-1; i>0; i--) {
        x[i] = x[i-1];
        y[i] = y[i-1];
    }

    x[0] = new_x;
    y[0] = new_y;

    return new_y;
  }

  void refresh()
  {
    for (int i=0; i<N; i++) {
      x[i] = 0;
      y[i] = 0;
    }
  }

  
}