public class sendingMailAttachment {

    public String cmail { get; set; }

    public String name { get; set; }
   
   // public sendingMailAttachment (){}
    public pagereference Submit(){
    
    Case Conval=new Case();
    conval.lastname=name ;
    conval.ownerId=cmail ;
    insert conval;
    
        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.info, 'case created');
        ApexPages.addMessage(myMsg); 
        // Reference the attachment page and pass in the  ID
        PageReference pdf =  Page.emailAttachmentEx1; //when we save page it will create vf page which is emailAttachmentEx1
        pdf.getParameters().put('id',conval.id);
        // Take the PDF content
        Blob b = pdf.getContentAsPDF();
        // Create the email attachment
        Messaging.EmailFileAttachment efa = new Messaging.EmailFileAttachment();
        efa.setContentType('application/pdf');
        efa.setFileName('attachment.pdf');
        efa.setInline(false);
        efa.setBody(b);
        
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
        String[] toAddresses = new String[] {conval.Email};
        mail.setToAddresses(toAddresses);
        mail.setSubject('New Case is created'); 
        
        mail.plainTextBody = 'Name : '+conval.lastname+'\n'+'Mail Id : '+conval.email;
        
        mail.setFileAttachments(new Messaging.EmailFileAttachment[] {efa});
        Messaging.SendEmail(new Messaging.SingleEmailMessage[] {mail});
        return null;
    }
    
}