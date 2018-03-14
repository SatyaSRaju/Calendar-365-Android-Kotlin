package twitter.sr.com.twitterdemo

/**
 * Created by SRaju on 7/1/17.
 */

class ImgPostInfo {
    var UserUID:String? = null
    var Message:String? = null
    var PostedImage:String? = null

    constructor(UserUID:String, Message:String, PostedImage:String) {
        this.UserUID = UserUID
        this.Message = Message
        this.PostedImage = PostedImage
    }

}