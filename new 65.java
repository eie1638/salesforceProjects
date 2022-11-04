public class sendingemail2 {

    public string Id {get;set;}
    public string casenum2 {get;set;}
    public list<case> l;
    public string Status2 {get;set;}
    public string Description {get;set;}
    public string Origin {get;set;}
    public string types {get;set;}
    public sendingemail2(){
        casenum2 = apexpages.currentPage().getParameters().get('casenum2');
        Status2 = apexpages.currentPage().getParameters().get('Status');
         Origin = apexpages.currentPage().getParameters().get('Origin');
         Description = apexpages.currentPage().getParameters().get('Description');
        types = apexpages.currentPage().getParameters().get('types');
        
    }
}