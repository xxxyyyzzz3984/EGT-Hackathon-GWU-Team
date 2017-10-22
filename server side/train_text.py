import csv
import re
import cPickle as pickle
import os.path
import tensorflow as tf
import numpy as np
from tensorflow.contrib import rnn

word2int_file = "./train data/word2int.p"
train_text_csf_filepath = "./train data/text_emotion.csv"

def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)

def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)

def lstm_cell(num_units):
  return tf.contrib.rnn.BasicLSTMCell(num_units)

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


train_x = []
train_y = []
text2num_dict = dict()
index = 1
with open(train_text_csf_filepath, 'rb') as train_text_csv:
    train_csv_reader = csv.reader(train_text_csv)
    for line in train_csv_reader:
        text = line[3]
        text = re.sub('[^A-Za-z0-9 @]+', '', text)
        text_words = text.split(' ')

        for text_word in text_words:
            if "@" in text_word:
                text_words.remove(text_word)
            elif "" == text_word:
                text_words.remove(text_word)

        for i in range(len(text_words)):
            text_words[i] = text_words[i].lower()
            if text_words[i] not in text2num_dict:
                text2num_dict[text_words[i]] = index
                index += 1
                text_words[i] = text2num_dict[text_words[i]]

            else:
                text_words[i] = text2num_dict[text_words[i]]


        ## if the length of text words is 0, ignore
        if len(text_words) < 1:
            continue

        emotion_label = line[1]
        if "anger" in emotion_label:
            train_y.append([1, 0])

        elif "boredom" in emotion_label:
            train_y.append([1, 0])

        elif "enthusiasm" in emotion_label:
            train_y.append([0, 1])

        elif "fun" in emotion_label:
            train_y.append([0, 1])

        elif "happiness" in emotion_label:
            train_y.append([0, 1])

        elif "hate" in emotion_label:
            train_y.append([1, 0])

        elif "love" in emotion_label:
            train_y.append([0, 1])

        elif "neutral" in emotion_label:
            train_y.append([0, 1])

        elif "relief" in emotion_label:
            train_y.append([0, 1])

        elif "sadness" in emotion_label:
            train_y.append([1, 0])

        elif "surprise" in emotion_label:
            train_y.append([0, 1])

        elif "worry" in emotion_label:
            train_y.append([1, 0])

        else:
            continue

        train_x.append(text_words)

for j in range(len(train_x)):
    if len(train_x[j]) < 60:
        for n in range(60 - len(train_x[j])):
            train_x[j].append(0)


## save the word index if not exists
if not os.path.isfile(word2int_file):
    with open(word2int_file, 'wb') as fp:
        pickle.dump(text2num_dict, fp)


### RNN Training Part ###
train_x = np.array(train_x)
train_y = np.array(train_y)
learning_rate = 1e-6
num_input = 5
timestamps = train_x.shape[1]/5
num_neurons = timestamps
num_classifications = train_y.shape[1]

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

'''Training phase'''
loss_op = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(
    logits=prediction, labels=y_placeholder))

mse = tf.reduce_mean(tf.square(prediction-y_placeholder))

optimizer = tf.train.AdamOptimizer(learning_rate)
train_op = optimizer.minimize(loss_op)
correct_pred = tf.equal(tf.argmax(prediction, 1), tf.argmax(y_placeholder, 1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))

# Initialize the variables (i.e. assign their default value)
init = tf.global_variables_initializer()

# Start training
with tf.Session() as sess:
    # Run the initializer
    sess.run(init)
    saver = tf.train.Saver()
    try:
        saver.restore(sess, './train_model.ckpt')
        print("Model restored.")
    except:
        pass
    train_x = train_x.reshape([train_x.shape[0], timestamps, num_input])



    step = 0
    while True:
        # train_x_batch = train_x[step * 500: step * 500 + 500]
        # train_y_batch = train_y[step * 500: step * 500 + 500]
        sess.run(train_op, feed_dict={x_placeholder: train_x, y_placeholder: train_y})

        loss, acc = sess.run([loss_op, accuracy], feed_dict={x_placeholder: train_x,
                                                             y_placeholder: train_y})


        if step > 100:
            print "One round finished"
            print 'saving the model'
            saver.save(sess, save_path='./train_model.ckpt')
            step = 0

        step += 1

        print loss, acc

        if acc > 0.91:
            print 'saving the model and exit'
            saver.save(sess, save_path='./train_model.ckpt')
            break
