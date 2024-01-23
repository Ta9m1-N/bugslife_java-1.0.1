package com.example.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Controller
public class GlobalExceptionHandler extends DefaultErrorAttributes {

	@Autowired
	private Environment environment;

	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, WebRequest request, Model model) {
		HttpStatus status = getStatus(request);
		if (status == HttpStatus.NOT_FOUND) {
			return "error/404";
		} else {
			String propertyValue = environment.getProperty("server.error.include-stacktrace");
			if (!"never".equals(propertyValue)) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				model.addAttribute("stackTrace", sw.toString());
			}

			model.addAttribute("error", ex.getMessage());
			return "error/500";
		}
	}

	private HttpStatus getStatus(WebRequest request) {
		Object status = request.getAttribute("javax.servlet.error.status_code", WebRequest.SCOPE_REQUEST);
		if (status != null) {
			return HttpStatus.valueOf((Integer)status);
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
