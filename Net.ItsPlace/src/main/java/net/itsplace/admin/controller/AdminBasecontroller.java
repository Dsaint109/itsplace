package net.itsplace.admin.controller;


import net.itsplace.admin.service.AdminBaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AdminBasecontroller {
	private static final Logger logger = LoggerFactory.getLogger(AdminBasecontroller.class);
	@Autowired
	private AdminBaseService adminBaseService;
}
