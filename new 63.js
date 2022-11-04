<apex:page Standardcontroller="case" extensions="sendingMailAttachment">
<apex:form >
 <apex:pageBlock title="Case">
    <apex:pageBlockSection title="case information">
        <apex:inputField value="{!case.Status}" label="Status"/>
        <apex:inputField value="{!case.type}" label="types"/>
        <apex:inputField value="{!case.Origin}" label="Origin"/>
        <apex:inputField value="{!case.Description}" label="Description"/>
       
        
     </apex:pageBlockSection>
     <apex:pageBlockButtons location="Bottom">
          <apex:commandButton action="{!Submit}" value="Save"/> 
     </apex:pageBlockButtons>
     
    </apex:pageBlock>
 </apex:form>
 </apex:page>