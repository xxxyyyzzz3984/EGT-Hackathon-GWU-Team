import tensorflow as tf
import numpy as np
from tensorflow.contrib import rnn
import pickle
import re

word2int_filepath = "./train data/word2int.p"

def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)

def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)

def lstm_cell(num_units):
  return tf.contrib.rnn.BasicLSTMCell(num_units, reuse=tf.get_variable_scope().reuse)

def RNN(x, timestamps, num_neurons, weights, biases):
    x = tf.unstack(x, timestamps, 1)
    lstm_cell = rnn.BasicLSTMCell(num_neurons, forget_bias=1.0)
    outputs, states = rnn.static_rnn(lstm_cell, x, dtype=tf.float32)
    return tf.matmul(outputs[-1], weights) + biases

def multi_RNN(x, timestamps, num_neurons, weights, biases, num_layers):
    x = tf.unstack(x, timestamps, 1)
    stacked_lstm = tf.contrib.rnn.MultiRNNCell(
        [tf.contrib.rnn.BasicLSTMCell(num_neurons) for _ in range(num_layers)])
    outputs, states = rnn.static_rnn(stacked_lstm, x, dtype=tf.float32)
    return tf.matmul(outputs[-1], weights) + biases

class TextClassifier:
    def __init__(self, posts_list):
        ## convert post to int vector
        tf.reset_default_graph()
        self.pred = None
        with open(word2int_filepath, 'rb') as f:
            word2int_index = pickle.load(f)

        text_x = []
        for post_string in posts_list:
            text_x_onerow = []
            post_string = re.sub('[^A-Za-z0-9 @]+', '', post_string)
            text_words = post_string.split(' ')
            for text_word in text_words:
                if "@" in text_word:
                    text_words.remove(text_word)
                elif "" == text_word:
                    text_words.remove(text_word)

            for i in range(60):
                if i > len(text_words) - 1:
                    text_x_onerow.append(0)
                else:
                    if text_words[i] in word2int_index:
                        text_x_onerow.append(word2int_index[text_words[i]])
                    else:
                        text_x_onerow.append(0)

            text_x.append(text_x_onerow)

        ### RNN Training Part ###
        text_x = np.array(text_x)
        print text_x.shape

        learning_rate = 1e-3
        num_input = 5
        timestamps = text_x.shape[1] / 5
        num_neurons = timestamps
        num_classifications = 2

        text_x = text_x.reshape(text_x.shape[0], timestamps, num_input)

        x_placeholder = tf.placeholder("float", shape=[None, timestamps, num_input])
        y_placeholder = tf.placeholder("float", shape=[None, num_classifications])

        #### fully connected layers ####
        rnn_W_fc1 = weight_variable([timestamps, timestamps])
        rnn_b_fc1 = bias_variable([timestamps])
        rnn_h_flat = tf.reshape(x_placeholder, [-1, timestamps])  # flat into 1 dimension
        rnn_h_fc1 = tf.nn.relu(tf.matmul(rnn_h_flat, rnn_W_fc1) + rnn_b_fc1)

        rnn_W_fc2 = weight_variable([timestamps, timestamps])
        rnn_b_fc2 = bias_variable([timestamps])
        rnn_h_fc2 = tf.nn.relu(tf.matmul(rnn_h_fc1, rnn_W_fc2) + rnn_b_fc2)

        rnn_W_fc3 = weight_variable([timestamps, timestamps])
        rnn_b_fc3 = bias_variable([timestamps])
        rnn_h_fc3 = tf.nn.relu(tf.matmul(rnn_h_fc2, rnn_W_fc3) + rnn_b_fc3)

        rnn_h_fc3 = tf.reshape(rnn_h_fc3, [-1, timestamps, num_input])

        ####### LSTM layers for label #########
        w_lstm1 = weight_variable([num_neurons, num_classifications])
        b_lstm1 = bias_variable([num_classifications])
        prediction = tf.nn.softmax(RNN(rnn_h_fc3, timestamps, num_neurons, w_lstm1, b_lstm1))

        with tf.Session() as sess:
            saver = tf.train.Saver()
            saver.restore(sess, './train_model.ckpt')
            print "Model Restored"
            self.pred = prediction.eval({x_placeholder: text_x}, sess)

    def get_prediction(self):
        return self.pred
