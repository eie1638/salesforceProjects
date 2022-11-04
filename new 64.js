<apex:page controller="sendingemail2">
    
<apex:form>
 <apex:pageBlock title="Case">
    <apex:pageBlockSection title="CASE INFORMATION">
        
        <table>
            <tr style="padding:4px;font-size:14px;">
                <td style="padding:6px">CASE NUMBER : {!casenum2}</td>
                <td style="padding:6px">TYPE : {!types}</td>
            </tr>
           <tr style="padding:4px;font-size:14px;">
                <td style="padding:6px">ORIGIN : {!Origin}</td>
                <td>STATUS : {!Status2}</td>
            </tr>
           <tr style="padding:4px;font-size:14px;">
                <td style="padding:6px">DESCRIPTION : {!Description}</td>
            </tr>
        </table>
   <!--      <apex:outputText value="{!Status2}" label="Status"> </apex:outputText>
         
        <apex:outputText value="{!types}" label="types"> </apex:outputText>
        <apex:outputText value="{!Origin}" label="Origin"> </apex:outputText>
        <apex:outputText value="{!casenum2}" label="Case number"> </apex:outputText>
        <apex:outputText value="{!Description}" label="Description"> </apex:outputText> -->
        
     </apex:pageBlockSection>
     
    </apex:pageBlock>
 </apex:form>
    
 </apex:page>