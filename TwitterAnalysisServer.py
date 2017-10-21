import json
import requests
from bs4 import BeautifulSoup
import copy

class TwitterCrawl:
    def __init__(self, username, crawl_times):
        self.url = "https://twitter.com/%s?lang=en" % username
        content = requests.get(self.url).text.encode('utf-8')
        self.soup = BeautifulSoup(content, 'lxml')
        self.profile_img_link = ''
        self.posts_info = []
        self.bio = ''
        self.location = ''
        self.__get_tweets(crawl_times)
        self.__get_profile()

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

                try:
                    content = requests.get(post_link).text.encode('utf-8')
                    post_location = BeautifulSoup(content, 'lxml').find('a', {'data-place-id': True})
                    post_info['location'] = post_location

                except:
                    post_info['location'] = ''
                    print "post without location"

                self.posts_info.append(copy.copy(post_info))

                next_data_pos = self.soup.find('div',
                        {'class': 'stream-container  '})['data-min-position']


            for i in range(crawl_times):
                self.url = "https://twitter.com/i/profiles/show/muftimenk/timeline/tweets?composed_count=0&include_available_features=1" \
                           "&include_entities=1&include_new_items_bar=true&interval=30000&lang=en&latent_count=0&max_position=" + next_data_pos
                content = requests.get(self.url).text.encode('utf-8')
                json_data = json.loads(content)
                updated_html_json = json_data['items_html']
                self.soup = BeautifulSoup(updated_html_json, 'lxml')
                continued_posts_info = self.soup.findAll('div', {'data-permalink-path': True})
                print continued_posts_info
                for continued_post_info in continued_posts_info:
                    time_ms = continued_post_info.find('span', {'data-time-ms': True})['data-time-ms']
                    continued_post = continued_post_info.find('p', {'class':
                                                              'TweetTextSize TweetTextSize--normal js-tweet-text tweet-text'}).text
                    post_link = 'https://www.twitter.com/' + continued_post_info['data-permalink-path']
                    post_info['time_ms'] = time_ms
                    post_info['post_content'] = continued_post

                    try:
                        content = requests.get(post_link).text.encode('utf-8')
                        post_location = BeautifulSoup(content, 'lxml').find('a', {'data-place-id': True})
                        post_info['location'] = post_location

                    except:
                        post_info['location'] = ''
                        print "post without location"

                    self.posts_info.append(copy.copy(post_info))

                next_data_pos = json_data['min_position']


def crawl_tweets_on_tags():
    pass

username = 'muftimenk'
crawl_times = 0
tweetcrawl = TwitterCrawl(username, crawl_times)