/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

/**
 * The procedure used to train the weight model, in particular, how
 * excess/defects are distributed among the characters: uniform distribution,
 * linear distribution (proportional to the character's weight) or random
 * distribution.
 *
 * @author rafa
 */
public enum TrainingMethod {

    UNIFORM, LINEAR, RANDOM
}
