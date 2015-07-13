package com.company;

import static java.lang.String.*;

public class Element {
    private String _type = null;
    private int _num, _nump, _numm;
    private double _val;
    public Element(String type, int num, int nodep, int nodem, double val)
    {
        switch (type) {
            case "V":
            case "I":
            case "R":
            case "C":
            case "L":
                _nump=nodep; _numm=nodem; _val=val; _num=num; _type=type;
                break;
            case "D":
                _nump=nodep; _numm=nodem; _num=num; _type=type;
        }
    }

    public double get_val() {
        return _val;
    }

    public int get_num() {
        return _num;
    }

    public int get_numm() {
        return _numm;
    }

    public int get_nump() {
        return _nump;
    }

    public String get_type() {
        return _type;
    }

    public void set_num(int _num) {
        this._num = _num;
    }

    public void set_numm(int _numm) {
        this._numm = _numm;
    }

    public void set_nump(int _nump) {
        this._nump = _nump;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_val(double _val) {
        this._val = _val;
    }

    @Override
    public String toString() {
        return format("%s%d %d %d %s", _type, _num, _nump, _numm, (_type != "D") ? _val : "");
    }
}
