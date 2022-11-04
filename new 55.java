public class sendingemail2 {

    public string Id;
    public string Status2 {get;set;}
    public string Description {get;set;}
    public string Origin {get;set;}
    public string types {get;set;}
    public sendingemail2(){
        Id = apexpages.currentPage().getParameters().get('Id');
        Status2 = apexpages.currentPage().getParameters().get('Status');
         Origin = apexpages.currentPage().getParameters().get('Origin');
         Description = apexpages.currentPage().getParameters().get('Description');
        types = apexpages.currentPage().getParameters().get('types');
        
    }
}