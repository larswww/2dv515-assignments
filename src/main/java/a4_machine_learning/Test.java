package a4_machine_learning;

import a2_clustering.FileSysDB;
import a4_machine_learning.dataSetReader.ARFFreader;
import a4_machine_learning.dataSetReader.Dataset;
import a4_machine_learning.dataSetReader.Result;
import libsvm.*;
import org.jsoup.select.Evaluator;
import org.junit.*;
import org.junit.Assert;


import org.junit.rules.ErrorCollector;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;


public class Test {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @org.junit.Test
    public void naiveBayesWikipedia() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/wikipedia_70.arff", new NaiveBayesMultinomialText());
    }

    @org.junit.Test
    public void decisionTreesFIFA() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/FIFA_skill.arff", new J48());
    }

    @org.junit.Test
    public void neuralNetMultilayer() {
        init("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/matchmaker_fixed.arff", new MultilayerPerceptron());
    }

    @org.junit.Test
    public void ownNaiveBayesFifa() {
        ARFFreader arfr = new ARFFreader();
        arfr.readFile("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/FIFA_skill_nominal.arff");
        Dataset data = arfr.getData();
        ArrayList<String> cats = data.getDistinctClassValues().getNominalValues();
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);


        ArrayList<a4_machine_learning.dataSetReader.Instance> inst = data.toList();

        for (a4_machine_learning.dataSetReader.Instance i : inst) {
            StringBuilder sb = new StringBuilder();
            for (String item : i.getAttributeArrayNominal()) {
                if (item != null) sb.append(item).append(" ");
            }

            for (Double numItem : i.getAttributeArrayNumerical()) {
                if (numItem != 0.0) sb.append(numItem);
            }

            cl.train(sb.toString(), i.getClassAttribute().nominalValue());
        }

        System.out.println("good" + cl.classify("great yes middle"));
        System.out.println("good" + cl.classify("average yes many"));
        System.out.println("good" + cl.classify("average no many"));
        System.out.println("good" + cl.classify("junk no many"));

        System.out.println("bad" + cl.classify("junk no few"));
        System.out.println("bad" + cl.classify("average yes few"));
        System.out.println("bad" + cl.classify("great yes few"));
        System.out.println("bad" + cl.classify("average no few"));
        System.out.println("bad" + cl.classify("average no middle"));
        System.out.println("bad" + cl.classify("great yes few"));
        System.out.println("bad" + cl.classify("junk no many"));
        System.out.println("bad" + cl.classify("junk yes few"));
        System.out.println("bad" + cl.classify("great no few"));
        System.out.println("bad" + cl.classify("average yes few"));

    }

    @org.junit.Test
    public void ownNaiveBayesWiki() {
        ARFFreader arfr = new ARFFreader();
        arfr.readFile("/Users/mbp/Documents/Code/2dv515/assignments/data/a4/wikipedia_70.arff");
        Dataset data = arfr.getData();
        ArrayList<String> cats = data.getDistinctClassValues().getNominalValues();
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);


        ArrayList<a4_machine_learning.dataSetReader.Instance> inst = data.toList();
        HashMap<String, String> catItem = new HashMap<>();

        for (a4_machine_learning.dataSetReader.Instance i : inst) {
            cl.train(i.getAttribute(0).value(), i.getAttribute(1).value());
            catItem.put(i.getAttribute(0).value(), i.getAttribute(1).value());
        }

        for (String key : catItem.keySet()) {
            System.out.println(catItem.get(key) + " " +cl.classify(key));
        }



    }


    @org.junit.Test
    public void bookPage121() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("good");
        cats.add("bad");
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);
        cl.train("the quick brown fox jumps over the lazy dog", "good");
        cl.train("make quick money in the online casino", "bad");
        System.out.println(cl.fcount("quick", "good"));
        System.out.println(cl.fcount("quick", "bad"));

    }

    @org.junit.Test
    public void bookPage122() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("good");
        cats.add("bad");
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);
        sampletrain(cl);
        System.out.println(cl.fprob("quick", "good"));
        System.out.println("Expected 0.66666666666666663");

    }
    @org.junit.Test
    public void bookNaiveBayes() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("good");
        cats.add("bad");
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);
        sampletrain(cl);
        System.out.println(cl.prob("quick rabbit", "good"));
        System.out.println("expected 0.15624999999999997");
        System.out.println(cl.prob("quick rabbit", "bad"));
        System.out.println("expected 0.050000000000000003");

    }

    @org.junit.Test
    public void bookPage123() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("good");
        cats.add("bad");
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);
        sampletrain(cl);
        System.out.println(cl.weightedprob("money", "good", 1.0, 0.5));
        System.out.println("Expected 0.25");
        sampletrain(cl);
        System.out.println(cl.weightedprob("money", "good", 1.0, 0.5));
        System.out.println("Expected 0.16");

    }

    @org.junit.Test
    public void bookClassify() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("good");
        cats.add("bad");
        OwnClassifier cl = new OwnClassifier(new GetWords(), cats);
        sampletrain(cl);
        System.out.println(cl.classify("quick rabbit"));
        System.out.println("Expected: good");
        System.out.println(cl.classify("quick money"));
        System.out.println("Expected: bad");
        cl.setthresholds("bad", 3.0);
        System.out.println("Expected: unknown");
        System.out.println(cl.classify("quick money"));

        for (int i = 0; i < 10; i++) {
            sampletrain(cl);

        }

        System.out.println(cl.classify("quick money"));
        System.out.println("expected: bad");


    }

    private void sampletrain(OwnClassifier cl) {
        cl.train("Nobody owns the water.","good");
        cl.train("the quick rabbit jumps fences","good");
        cl.train("buy pharmaceuticals now","bad");
        cl.train("make quick money at the online casino","bad");
        cl.train("the quick brown fox jumps","good");
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
        eval.evaluateCV();

    }

    private Instances readData(String filename) {

        try {
            //read raw data (all words are i one string)
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);
             Instances data = source.getDataSet();
//            Instances raw = source.getDataSet();
//
//            // convert to bag-of-words using teh StringToWordVector filter
//            StringToWordVector stw = new StringToWordVector(10000);
//            stw.setLowerCaseTokens(true);
//            stw.setInputFormat(raw);

//            Instances data = Filter.useFilter(raw, stw);
            /* if StringToWordVector is used, Weka puts the class attribute first
            (in contract to the default where class attribut is last)
             */
            data.setClassIndex(data.numAttributes() - 1);


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
