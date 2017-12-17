package a4_machine_learning;

import a2_clustering.FileSysDB;
import a4_machine_learning.dataSetReader.Dataset;
import a4_machine_learning.dataSetReader.Result;
import libsvm.*;
import org.jsoup.select.Evaluator;
import org.junit.*;


import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.*;

import java.util.ArrayList;


public class Test {

    @org.junit.Test
    public void naiveBayesWikipedia() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/wikipedia_70.arff", new NaiveBayesMultinomial());
    }

    @org.junit.Test
    public void decisionTreesFIFA() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/FIFA_skill.arff", new J48());
    }

    @org.junit.Test
    public void neuralNetMultilayer() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/matchmaker_fixed.arff", new MultilayerPerceptron());
    }

    private void init(String filename, Classifier cl) {
        Instances data = readData(filename);
        Classifier cla = train(data, cl);
        test(data, cla);

    }

    @org.junit.Test
    public void toLibSVMDataStruct()  {

        libsvmClassifier svm = new libsvmClassifier();
        a4_machine_learning.dataSetReader.Evaluator eval = new a4_machine_learning.dataSetReader.Evaluator(svm, "/Users/mbp/Documents/Code/2dv515/assignments/data/a4/matchmaker_fixed.arff");
        eval.evaluateWholeSet();
        eval.printDataset();

    }


    private Instances readData(String filename) {

        try {
            //read raw data (all words are i one string)
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);
            Instances raw = source.getDataSet();

            // convert to bag-of-words using teh StringToWordVector filter
            StringToWordVector stw = new StringToWordVector(10000);
            stw.setLowerCaseTokens(true);
            stw.setInputFormat(raw);

            Instances data = Filter.useFilter(raw, stw);
            /* if StringToWordVector is used, Weka puts the class attribute first
            (in contract to the default where class attribut is last)
             */
            data.setClassIndex(0);


            return data;


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        return null;
    }

    private Classifier train(Instances data, Classifier cl) {

        try {
            cl.buildClassifier(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return cl;

    }

    private void test(Instances data, Classifier cl) {

        try {
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(cl, data, 10, new java.util.Random(1));
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toMatrixString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
