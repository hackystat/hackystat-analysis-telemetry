   <table>
     <tr>
        <td><input:radio name="IntervalType" value="Day" default="<%= (String)request.getAttribute(\"IntervalType\") %>"/>Day</td>
        <td>
          Start 
        </td>  
        
        <td><input:select name="DayInterval.StartDay"
               options="<%= (java.util.TreeMap) request.getAttribute(\"DayOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.StartDayDefault\") %>"
            />
         
            <input:select name="DayInterval.StartMonth"
               options="<%= (java.util.TreeMap) request.getAttribute(\"MonthOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.StartMonthDefault\") %>"
            />
            <input:select name="DayInterval.StartYear"
               options="<%= (java.util.TreeMap) request.getAttribute(\"YearOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.StartYearDefault\") %>"
            />
        </td>       
        <td>
          End  
        </td>  
        <td>
        	<input:select name="DayInterval.EndDay"
               options="<%= (java.util.TreeMap) request.getAttribute(\"DayOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.EndDayDefault\") %>"
            />
            <input:select name="DayInterval.EndMonth"
               options="<%= (java.util.TreeMap) request.getAttribute(\"MonthOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.EndMonthDefault\") %>"
            />
            <input:select name="DayInterval.EndYear"
               options="<%= (java.util.TreeMap) request.getAttribute(\"YearOptions\") %>"
               default="<%= (String)request.getAttribute(\"DayInterval.EndYearDefault\") %>"
            />
         </td>   
     </tr>

     <tr>
       <td><input:radio name="IntervalType" value="Week" default="<%= (String)request.getAttribute(\"IntervalType\") %>"/>Week</td>
       <td>
          Start 
       </td>  

       <td> 
          <input:select name="WeekInterval.StartWeek"
               options="<%= (java.util.TreeMap) request.getAttribute(\"WeekOptions\") %>"
               default="<%= (String)request.getAttribute(\"WeekInterval.StartWeekDefault\") %>"
           />
       </td>  
       <td>
         End
       </td>
       <td>  
          <input:select name="WeekInterval.EndWeek"
               options="<%= (java.util.TreeMap) request.getAttribute(\"WeekOptions\") %>"
               default="<%= (String)request.getAttribute(\"WeekInterval.EndWeekDefault\") %>"
           />
       </td>    
     </tr>
     
     <tr>
        <td><input:radio name="IntervalType" value="Month" default="<%= (String)request.getAttribute(\"IntervalType\") %>" />Month</td>
        <td>
          Start 
        </td>  
        
        <td> 
            <input:select name="MonthInterval.StartMonth"
               options="<%= (java.util.TreeMap) request.getAttribute(\"MonthOptions\") %>"
               default="<%= (String)request.getAttribute(\"MonthInterval.StartMonthDefault\") %>"
            />
            <input:select name="MonthInterval.StartYear"
               options="<%= (java.util.TreeMap) request.getAttribute(\"YearOptions\") %>"
               default="<%= (String)request.getAttribute(\"MonthInterval.StartYearDefault\") %>"
            />
        </td> 
        <td> End </td>
        <td>
            <input:select name="MonthInterval.EndMonth"
	               options="<%= (java.util.TreeMap) request.getAttribute(\"MonthOptions\") %>"
                   default="<%= (String)request.getAttribute(\"MonthInterval.EndMonthDefault\") %>"
             />
            <input:select name="MonthInterval.EndYear"
	               options="<%= (java.util.TreeMap) request.getAttribute(\"YearOptions\") %>"
                   default="<%= (String)request.getAttribute(\"MonthInterval.EndYearDefault\") %>"
            />
        </td>    
     </tr>
  </table> 