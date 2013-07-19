package test.twitter

object TwitterFixture {
	
	def fullMessage = {
		"""
			[{
	    "created_at": "Wed Oct 24 21:34:06 +0000 2012",
	    "id": 261219035964915700,
	    "id_str": "261219035964915712",
	    "text": "#Play20StartApp on Cloudbees. Demo: http://t.co/HZCphh5J\nSrc:\nhttps://t.co/MmWHbDGD\n#playframework #play2 cc @cloudbees @sguernion @ybonnel",
	    "source": "<a href=\"http://itunes.apple.com/us/app/twitter/id409789998?mt=12\" rel=\"nofollow\">Twitter for Mac</a>",
	    "truncated": false,
	    "in_reply_to_status_id": null,
	    "in_reply_to_status_id_str": null,
	    "in_reply_to_user_id": null,
	    "in_reply_to_user_id_str": null,
	    "in_reply_to_screen_name": null,
	    "user": {
	        "id": 75519416,
	        "id_str": "75519416",
	        "name": "Yvonnick Esnault",
	        "screen_name": "yesnault",
	        "location": "",
	        "description": "https://github.com/yesnault",
	        "url": "http://www.braldahim.com",
	        "entities": {
	            "url": {
	                "urls": [
	                    {
	                        "url": "http://www.braldahim.com",
	                        "expanded_url": null,
	                        "indices": [
	                            0,
	                            24
	                        ]
	                    }
	                ]
	            },
	            "description": {
	                "urls": [ ]
	            }
	        },
	        "protected": false,
	        "followers_count": 58,
	        "friends_count": 134,
	        "listed_count": 1,
	        "created_at": "Sat Sep 19 10:08:52 +0000 2009",
	        "favourites_count": 88,
	        "utc_offset": 3600,
	        "time_zone": "Paris",
	        "geo_enabled": true,
	        "verified": false,
	        "statuses_count": 224,
	        "lang": "en",
	        "contributors_enabled": false,
	        "is_translator": false,
	        "profile_background_color": "1A1B1F",
	        "profile_background_image_url": "http://a0.twimg.com/images/themes/theme9/bg.gif",
	        "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme9/bg.gif",
	        "profile_background_tile": false,
	        "profile_image_url": "http://a0.twimg.com/profile_images/2238978588/5aa91ee86e08993bba470a84318929cc_normal.jpeg",
	        "profile_image_url_https": "https://si0.twimg.com/profile_images/2238978588/5aa91ee86e08993bba470a84318929cc_normal.jpeg",
	        "profile_link_color": "2FC2EF",
	        "profile_sidebar_border_color": "181A1E",
	        "profile_sidebar_fill_color": "252429",
	        "profile_text_color": "666666",
	        "profile_use_background_image": true,
	        "default_profile": false,
	        "default_profile_image": false,
	        "following": true,
	        "follow_request_sent": null,
	        "notifications": null
	    },
	    "geo": null,
	    "coordinates": null,
	    "place": null,
	    "contributors": null,
	    "retweet_count": 0,
	    "entities": {
	        "hashtags": [
	            {
	                "text": "Play20StartApp",
	                "indices": [
	                    0,
	                    15
	                ]
	            },
	            {
	                "text": "playframework",
	                "indices": [
	                    84,
	                    98
	                ]
	            },
	            {
	                "text": "play2",
	                "indices": [
	                    99,
	                    105
	                ]
	            }
	        ],
	        "urls": [
	            {
	                "url": "http://t.co/HZCphh5J",
	                "expanded_url": "http://play20startapp.yesnault.cloudbees.net",
	                "display_url": "play20startapp.yesnault.cloudbees.net",
	                "indices": [
	                    36,
	                    56
	                ]
	            },
	            {
	                "url": "https://t.co/MmWHbDGD",
	                "expanded_url": "https://github.com/yesnault/Play20StartApp/tree/cloudbees",
	                "display_url": "github.com/yesnault/Play2…",
	                "indices": [
	                    62,
	                    83
	                ]
	            }
	        ],
	        "user_mentions": [
	            {
	                "screen_name": "cloudbees",
	                "name": "CloudBees",
	                "id": 114758167,
	                "id_str": "114758167",
	                "indices": [
	                    109,
	                    119
	                ]
	            },
	            {
	                "screen_name": "sguernion",
	                "name": "Guernion Sylvain",
	                "id": 314251010,
	                "id_str": "314251010",
	                "indices": [
	                    120,
	                    130
	                ]
	            },
	            {
	                "screen_name": "ybonnel",
	                "name": "Bonnel Yan",
	                "id": 225864007,
	                "id_str": "225864007",
	                "indices": [
	                    131,
	                    139
	                ]
	            }
	        ]
	    },
	    "favorited": false,
	    "retweeted": false,
	    "possibly_sensitive": false
		}]
			"""
	}	
}