package twitter.sr.com.twitterdemo

/**
 * Created by SRaju on 6/29/17.
 */

class Tweet {

    var tweetID: String?=null
    var tweetText: String?=null
    var tweetImgURL: String?=null
    var tweetPersonUID: String?=null

    constructor(tweetID: String,tweetText: String, tweetImgURL: String, tweetPersonUID: String) {
        this.tweetID = tweetID
        this.tweetText = tweetText
        this.tweetImgURL = tweetImgURL
        this.tweetPersonUID = tweetPersonUID
    }
}