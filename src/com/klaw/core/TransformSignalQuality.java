/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klaw.core;

/**
 *
 * @author Klaw Strife
 */
public class TransformSignalQuality {
    public static float csq_dbm(float csq){
        return -113 + csq * 2;
    }
}
