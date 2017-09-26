package edu.phystech.stethoscope.player;

public class Polinom
{
  double[] polinom;
  
  void setSize(int size)
  {
    polinom = new double[size];
  }
  
  void setValue(int n, double value)
  {
    polinom[n] = value;
  }
  
  double getValue(int n)
  {
    return polinom[n];
  }
  
  int size()
  {
    return polinom.length;
  }
  
  Polinom multiply(Polinom a)
  {
    Polinom result = new Polinom();
    result.setSize(size()+a.size() - 1);

    for (int i=0; i<size(); i++)
        for (int j=0; j<a.size(); j++)
            result.setValue(i+j, result.getValue(i+j) + polinom[i]*a.getValue(j));;
    return result;
  }

  Polinom add(Polinom a)
  {
    int newSize = size()>a.size() ? size() : a.size();
    Polinom result = new Polinom();
    result.setSize(newSize);

    for (int i=0; i<size(); i++)
            result.setValue(i, result.getValue(i) + polinom[i]);
    for (int i=0; i<a.size(); i++)
            result.setValue(i, result.getValue(i) + a.getValue(i));
    
    return result;
  }
  
  void multiply(double d)
  {
    for (int i=0; i<size(); i++)
      polinom[i] *= d;
  }
  
  void add(double d)
  {
    polinom[0] += d;
  }
  
  Polinom substitute(Polinom sub)
  {
    Polinom result = new Polinom();
    result.setSize(1);

    for (int i=0; i<size(); i++) {
      Polinom curr = new Polinom();
      curr.setSize(1);
      curr.setValue(0, polinom[i]);
      
      for (int j=0; j<i; j++)
        curr = curr.multiply(sub);
      result = result.add(curr);
    }

    return result;
  }

  Polinom substitute(Polinom sub_top, Polinom sub_bot)
  {
    Polinom result = new Polinom();
    result.setSize(0);

    for (int i=0; i<size(); i++) {
      Polinom curr = new Polinom();
      curr.setSize(1);
      curr.setValue(0, polinom[i]);
      for (int j=0; j<size()-1; j++)
        curr = curr.multiply(j<i ? sub_top : sub_bot);
      result = result.add(curr);
    }

    return result;
  }
}

