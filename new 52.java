public class sendingMailAttachment {
private final case c;
   
     public String Status { get; set; }
     public String types { get; set; }
     public String Origin { get; set; }
      public String Description { get; set; }
    public case c1;
    
   
   public sendingMailAttachment (ApexPages.StandardController stdController){
       this.c = (case)stdController.getRecord();
   }
   
    public pagereference submit(){
    c1 = new Case();
       c1.Status = c.Status;
        c1.type = c.Type;
        c1.Origin = c.Origin;
        c1.Description =c.Description;
        insert c1;
         
    
    
    //    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.info, 'case created');
      //  ApexPages.addMessage(myMsg); 
        // Reference the attachment page and pass in the  ID
        PageReference pdf =  new PageReference('/apex/emailAttachmentEx2?Id='+c1.Id+'&Status='+c1.Status+'&Origin='+c1.Origin+'&Description='+c1.Description+'&types='+c1.type);
        pdf.setRedirect(true);
        //when we save page it will create vf page which is emailAttachmentEx1
        pdf.getParameters().put('id',c1.id);
        pdf.getParameters().put('Status',c1.status);
        pdf.getParameters().put('types',c1.type);
        pdf.getParameters().put('Origin',c1.Origin);
        // Take the PDF content
        Blob b = pdf.getContentAsPDF();
        // Create the email attachment
        Messaging.EmailFileAttachment efa = new Messaging.EmailFileAttachment();
        efa.setContentType('application/pdf');
        efa.setFileName('attachment.pdf');
        efa.setInline(false);
        efa.setBody(b);
        
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
        list<String>toAddresses = new list<String> {'souravthakur1638@gmail.com'};
        mail.setToAddresses(toAddresses);
        mail.setSubject('Correct New Case is created'); 
        
        mail.plainTextBody = 'Name : '+c.CaseNumber+'\n'+'Mail Id : ';
        
        mail.setFileAttachments(new Messaging.EmailFileAttachment[] {efa});
        Messaging.SendEmail(new Messaging.SingleEmailMessage[] {mail});
        return pdf;
    }
    
}