package services.post

import services.auth.providers.GitHub

object GithubPoster extends GenericPoster {

  override val authProvider = GitHub
    
  override def urlToPost(post:models.Post) = "https://api.github.com/gists"
  
  override def postContent(post:models.Post):String = {
    """{
      "description": """"+post.title+"""",
      "public": true,
      "files": {
        """"+post.title+"""": {
          "content": """"+post.message+""""
        }
      }
    }"""
  }

}