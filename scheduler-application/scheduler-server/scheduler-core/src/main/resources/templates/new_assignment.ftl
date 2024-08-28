From ${owner}:

Hello<#if recipient.name??> ${recipient.name}</#if>,

Please visit

${url}

to review the following schedule change.

Product: ${drop.product.name}
Drop: ${drop.name}
Build type: ${type}
Schedule: ${schedule.name}
Version: ${schedule.version}
Created by: ${createdBy}
<#if updatedBy??>Updated by: ${updatedBy}</#if>
