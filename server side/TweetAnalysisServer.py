import json
import requests
from bs4 import BeautifulSoup
import copy
import operator
import simplejson
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from classify_text import TextClassifier
import numpy as np

positive_words_filepath = "./train data/positive-words.txt"
negative_words_filepath = "./train data/negative-words.txt"

class TwitterCrawl:
    def __init__(self, username, crawl_times):
        self.url = "https://twitter.com/%s?lang=en" % username
        content = requests.get(self.url).text.encode('utf-8')
        self.soup = BeautifulSoup(content, 'lxml')
        self.target_name = ""
        self.profile_img_link = ''
        self.posts_info = []
        self.all_post_texts = []
        self.return_info = dict()
        self.bio = ''
        self.location = ''
        self.negative_words = dict()
        self.positive_words = dict()
        self.top5pos_words = dict()
        self.top5neg_words = dict()
        self.all_neg_words = []
        self.all_pos_words = []
        with open(positive_words_filepath) as positive_f:
            for line in positive_f:
                line = line.replace('\n', '').replace(' ', '')
                self.all_pos_words.append(line.lower())

        with open(negative_words_filepath) as negative_f:
            for line in negative_f:
                line = line.replace('\n', '').replace(' ', '')
                self.all_neg_words.append(line.lower())

        try:
            self.__get_profile()
            self.__get_tweets(crawl_times)
            self.return_info["target_name"] = self.target_name
            self.return_info["profile_image_link"] = self.profile_img_link
        except:
            print "fail to retrieve personal info"

    def __get_profile(self):
        self.target_name = self.soup.find('a',
             {'class': 'ProfileHeaderCard-nameLink u-textInheritColor js-nav'}).text
        try:
            self.profile_img_link = self.soup.find('img', {'class': 'ProfileAvatar-image '})['src']
        except:
            pass

        try:
            self.bio = self.soup.find('p', {'class': 'ProfileHeaderCard-bio u-dir'}).text
        except:
            pass

        try:
            self.location = self.soup.find('span', {'class': 'ProfileHeaderCard-locationText u-dir'}).text
        except:
            pass


    '''crawl_times is the number of requests sent'''
    '''each time is 20 tweets'''
    '''0 means 20 tweets for initial'''
    def __get_tweets(self, crawl_times):
        ''''crawl post info'''
        post_info = dict()
        init_posts_info = self.soup.findAll('div', {'data-permalink-path': True})
        if len(init_posts_info) > 0:
            for init_post_info in init_posts_info:
                time_ms = init_post_info.find('span', {'data-time-ms': True})['data-time-ms']
                init_post = init_post_info.find('p', {'class':
                        'TweetTextSize TweetTextSize--normal js-tweet-text tweet-text'}).text
                post_link = 'https://www.twitter.com//' + init_post_info['data-permalink-path']
                post_info['time_ms'] = time_ms
                post_info['post_content'] = init_post
                self.all_post_texts.append(copy.copy(init_post))

                try:
                    content = requests.get(post_link).text.encode('utf-8')
                    post_location = BeautifulSoup(content, 'lxml').find('a', {'data-place-id': True})
                    post_info['location'] = post_location

                except:
                    post_info['location'] = ''
                    print "post without location"

                self.posts_info.append(copy.copy(post_info))


            try:
                next_data_pos = self.soup.find('div', {'class': 'stream-container  '})['data-min-position']
            except:
                return


            for i in range(crawl_times):
                self.url = "https://twitter.com/i/profiles/show/muftimenk/timeline/tweets?composed_count=0&include_available_features=1" \
                           "&include_entities=1&include_new_items_bar=true&interval=30000&lang=en&latent_count=0&max_position=" + next_data_pos
                content = requests.get(self.url).text.encode('utf-8')
                json_data = json.loads(content)
                updated_html_json = json_data['items_html']
                self.soup = BeautifulSoup(updated_html_json, 'lxml')
                continued_posts_info = self.soup.findAll('div', {'data-permalink-path': True})
                for continued_post_info in continued_posts_info:
                    time_ms = continued_post_info.find('span', {'data-time-ms': True})['data-time-ms']
                    continued_post = continued_post_info.find('p', {'class':
                                                              'TweetTextSize TweetTextSize--normal js-tweet-text tweet-text'}).text
                    post_link = 'https://www.twitter.com/' + continued_post_info['data-permalink-path']
                    post_info['time_ms'] = time_ms
                    post_info['post_content'] = continued_post
                    self.all_post_texts.append(copy.copy(continued_post))

                    try:
                        content = requests.get(post_link).text.encode('utf-8')
                        post_location = BeautifulSoup(content, 'lxml').find('a', {'data-place-id': True})
                        post_info['location'] = post_location

                    except:
                        post_info['location'] = ''
                        print "post without location"

                    self.posts_info.append(copy.copy(post_info))

                next_data_pos = json_data['min_position']

    # def CriminalityAnalysis(self):
    #
    #     for post_info in self.posts_info:
    #         post_info["post_content"]

    def posts_simple_analysis(self):
        if self.target_name == "":
            self.return_info = dict()
            return
        for post_info in self.posts_info:
            post_words = post_info['post_content'].split(' ')

            for post_word in post_words:
                post_word = post_word.lower()
                if post_word in self.all_pos_words:
                    if post_word not in self.positive_words:
                        self.positive_words[post_word] = 1
                    else:
                        self.positive_words[post_word] += 1

                if post_word in self.all_neg_words:
                    if post_word not in self.negative_words:
                        self.negative_words[post_word] = 1
                    else:
                        self.negative_words[post_word] += 1


        self.top5pos_words = dict(
            sorted(self.positive_words.iteritems(), key=operator.itemgetter(1), reverse=True)[:5])

        self.top5neg_words = dict(
            sorted(self.negative_words.iteritems(), key=operator.itemgetter(1), reverse=True)[:5])

        self.return_info["top5poswords"] = self.top5pos_words
        self.return_info["top5negwords"] = self.top5neg_words
        self.return_info["total_poswords"] = len(self.positive_words)
        self.return_info["total_negwords"] = len(self.negative_words)
        try:
            self.return_info["poswords_percent"] = \
            float(self.return_info["total_poswords"])/float(self.return_info["total_poswords"] + self.return_info["total_negwords"])
        except:
            self.return_info["poswords_percent"] = 0

        try:
            self.return_info["negwords_percent"] = \
            float(self.return_info["total_negwords"]) / float(
                self.return_info["total_poswords"] + self.return_info["total_negwords"])
        except:
            self.return_info["negwords_percent"] = 0

    def CriminalityAnalysis(self):
        if self.target_name == "":
            self.return_info = dict()
            return
        post_classifier = TextClassifier(self.all_post_texts)
        pred = post_classifier.get_prediction()
        pred_mean = pred.mean(axis=0)
        print pred_mean[1]
        self.return_info["pos_percentage"] = float(pred_mean[1])
        self.return_info["negative_percentage"] = float(pred_mean[0])
        print type(float(pred_mean[1]))

        if self.return_info["poswords_percent"] < 0.5 and self.return_info["negative_percentage"] > 0.9:
            self.return_info["criminal_alert"] = 1
        else:
            self.return_info["criminal_alert"] = 0

class S(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'json')
        self.end_headers()

    def do_POST(self):
        content_length = int(self.headers["Content-Length"])
        raw_data = self.rfile.read(content_length)
        self._set_headers()
        raw_data_parts = raw_data.split('\n')
        raw_data = raw_data_parts[1]
        raw_data = raw_data.replace('\r', '')
        print raw_data
        json_data = simplejson.loads(raw_data)
        username = json_data["username"]
        print "receive username " + username
        print "Process requests and analyzing ......"
        twittercrawl = TwitterCrawl(username, 0)
        twittercrawl.posts_simple_analysis()
        twittercrawl.CriminalityAnalysis()
        print "Processing finished, sending data......"
        response_json = json.dumps(twittercrawl.return_info)
        self.wfile.write(response_json)
        print "data sent."

def run(server_class=HTTPServer, handler_class=S, port=44444):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print 'Starting httpd...'
    httpd.serve_forever()

run()
# tweetcrawl = TwitterCrawl("abc", 0)
# tweetcrawl.posts_simple_analysis()
# tweetcrawl.CriminalityAnalysis()
# username = 'muftimenk'
# crawl_times = 0
# tweetcrawl = TwitterCrawl(username, crawl_times)
# print tweetcrawl.target_name
# test_string = ['zest', 'zest', 'zippy', 'zeal']
# negative_words = dict()
# positive_words = dict()
# all_negative_words = []
# all_positive_words = []
# with open(positive_words_filepath) as positive_f:
#     for line in positive_f:
#         line = line.replace('\n', '').replace(' ', '')
#         all_positive_words.append(line.lower())
#
# for post_word in test_string:
#     if post_word in all_positive_words:
#         post_word = post_word.lower()
#         if post_word not in positive_words:
#             positive_words[post_word] = 1
#         else:
#             positive_words[post_word] += 1
#
# post_analysis = dict()
# post_analysis["top_words"] = positive_words
# print post_analysis
