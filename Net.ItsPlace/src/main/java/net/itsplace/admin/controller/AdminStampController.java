package net.itsplace.admin.controller;

import net.itsplace.admin.service.AdminStampService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AdminStampController {
	private static final Logger logger = LoggerFactory.getLogger(AdminStampController.class);
	@Autowired
	private AdminStampService adminStampService;
}
