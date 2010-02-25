/*
 * $Id: TestBean.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.prop;

import java.util.Date;
import java.util.Map;

public class TestBean {

    int vi;
    double vf;
    String vs;
    Date vd;
    TestBean vo;
    Map vm;

    int[] ai;
    double[] af;
    String[] as;
    Date[] ad;
    TestBean[] ao;
    Map[] am;

    double[][] aaf;

    String onlyGetterAvailable;
    boolean wasSetterCalled = false;

    public TestBean() {
    }

    public int getVi() {
        return vi;
    }

    public void setVi(int vi) {
        this.vi = vi;
    }

    public double getVf() {
        return vf;
    }

    public void setVf(double vf) {
        this.vf = vf;
    }

    public String getVs() {
        return vs;
    }

    public void setVs(String vs) {
        this.vs = vs;
    }

    public Date getVd() {
        return vd;
    }

    public void setVd(Date vd) {
        this.vd = vd;
    }

    public TestBean getVo() {
        return vo;
    }

    public void setVo(TestBean vo) {
        this.vo = vo;
    }

    public Map getVm() {
        return vm;
    }

    public void setVm(Map vm) {
        this.vm = vm;
    }

    public int[] getAi() {
        return ai;
    }

    public void setAi(int[] ai) {
        this.ai = ai;
    }

    public double[] getAf() {
        return af;
    }

    public void setAf(double[] af) {
        this.af = af;
    }

    public String[] getAs() {
        return as;
    }

    public void setAs(String[] as) {
        this.as = as;
    }

    public Date[] getAd() {
        return ad;
    }

    public void setAd(Date[] ad) {
        this.ad = ad;
    }

    public TestBean[] getAo() {
        return ao;
    }

    public void setAo(TestBean[] ao) {
        this.ao = ao;
    }

    public Map[] getAm() {
        return am;
    }

    public void setAm(Map[] am) {
        this.am = am;
    }

    public double[][] getAaf() {
        return aaf;
    }

    public void setAaf(double[][] aaf) {
        this.aaf = aaf;
    }

    public String getOnlyGetterAvailable() {
        return onlyGetterAvailable;
    }

    public boolean isWasSetterCalled() {
        return wasSetterCalled;
    }

    public void setOnlySetterAvailable(String onlySetterAvailable) {
        wasSetterCalled = true;
    }
}
