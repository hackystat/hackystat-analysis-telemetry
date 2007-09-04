<%@ page errorPage="Error.jsp" %>
<%@ include file="Header.jsp" %>
<%@ include file="Command-ProjectCache.jsp" %>

<table id="ProjectCacheStatisticsTable">
<tr>
  <th class="data33" align="right">Hits:</th>
  <td class="data33" align="left"><c:out value="${Hits}"/> </td>
</tr>
<tr>
  <th class="data33" align="right">Misses/Additions:</th>
  <td class="data33" align="left"><c:out value="${Misses}"/> </td>
</tr>
<tr>
  <th class="data33" align="right">Clears:</th>
  <td class="data33" align="left"><c:out value="${Clears}"/> </td>
</tr>
</table>



