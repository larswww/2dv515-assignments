package a4_machine_learning;

import a4_machine_learning.dataSetReader.Classifier;
import a4_machine_learning.dataSetReader.Dataset;
import a4_machine_learning.dataSetReader.Instance;
import a4_machine_learning.dataSetReader.Result;
import libsvm.*;

public class libsvmClassifier implements Classifier{
    private Dataset data;
    private svm_model model;

    @Override
    public void train(Dataset train) {
        data = train;
        int n = data.noInstances();
        svm_problem prob = new svm_problem();
        prob.y = new double[n];
        prob.l = n;
        prob.x = new svm_node[n][data.noAttributes() - 1];

        for (int i = 0; i < data.noInstances(); i++) {
            Instance inst = data.getInstance(i);

            //attributes
            double[] vals = inst.getAttributeArrayNumerical();
            prob.x[i] = new svm_node[data.noAttributes() - 1];

            for (int a = 0; a < data.noAttributes() - 1; a++) {
                svm_node node = new svm_node();
                node.index = a;
                node.value = vals[a];
                prob.x[i][a] = node;
            }

            prob.y[i] = inst.getClassAttribute().numericalValue();

        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 100;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.cache_size = 20000;
        param.eps = 0.001;

        model = svm.svm_train(prob, param);

    }

    @Override
    public Result classify(Instance i) {


        double[] vals = i.getAttributeArrayNumerical();
        int no_classes = data.noClassValues();
        svm_node[] nodes = new svm_node[vals.length];

        for (int a = 0; a < vals.length; a++) {

                svm_node node = new svm_node();
                node.index = a;
                node.value = vals[a];
                nodes[a] = node;

        }

        int[] labels = new int[no_classes]; // does that work?
        svm.svm_get_labels(model, labels);
        double[] prob_estimates = new double[no_classes];

        double cVal = svm.svm_predict_probability(model, nodes, prob_estimates);

        return new Result(cVal);
    }
}
