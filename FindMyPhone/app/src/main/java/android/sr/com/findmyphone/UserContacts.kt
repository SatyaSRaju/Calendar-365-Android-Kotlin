package android.sr.com.findmyphone

/**
 * Created by SRaju on 7/20/17.
 */
class UserContacts {
    var contactName: String? = null
    var contactPhNumber: String? = null

    constructor(contactName: String, contactPhNumber: String)  {
        this.contactName = contactName
        this.contactPhNumber = contactPhNumber
    }
}