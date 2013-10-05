package models

object ColumnFixture {

  val jsonMsg = """{
    "title":"title2",
    "unifiedRequests":[
      {"service":"viadeo.wall","args":{}},
      {"service":"github.notifications","args":{"username":"manland"}}
    ],
    "index" : 1,
    "width": 1,
    "height": 3
  }"""
    
  val newJsonMsg = """{
    "title": "title2",
    "unifiedRequests": [
      {
        "service": "github.userEvents",
        "args": [
          {
            "name": "username",
            "value": {
              "display": "froggies",
              "call": "froggies",
              "avatarUrl": "",
              "description": ""
            }
          }
        ],
        "uidProviderUser": "u",
        "sinceId": [
        ]
      }
    ],
    "index": 0,
    "width": 2,
    "height": 2
  }"""
    
  val userJsonMsg = """{
    "options": {
      "isAdmin": false,
      "isPremium": false,
      "features": [
      ]
    },
    "accounts": [
      {
        "id": "a",
        "lastUse": 1380972229969,
        "name": "",
        "client": "Skimbo"
      }
    ],
    "distants": [
      {
        "id": "u",
        "socialType": "github",
        "token": {
          "token": "t",
          "secret": "",
          "refreshToken": null,
          "dateEnd": null
        },
        "username": null,
        "name": null,
        "description": null,
        "avatar": null
      }
    ],
    "columns": [
      {
        "title": "github userEvents",
        "unifiedRequests": [
        ],
        "index": 0,
        "width": 2,
        "height": 2
      }
    ]
}"""

}
