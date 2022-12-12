package com.cp.compiler.controllers;

import com.cp.compiler.executions.Execution;
import com.cp.compiler.executions.ExecutionFactory;
import com.cp.compiler.models.*;
import com.cp.compiler.models.testcases.ConvertedTestCase;
import com.cp.compiler.services.businesslogic.CompilerFacade;
import com.cp.compiler.wellknownconstants.WellKnownHeaders;
import com.cp.compiler.wellknownconstants.WellKnownParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Compiler Controller Class, this class exposes 4 endpoints for (Java, C, CPP, and Python)
 *
 * @author Zakaria Maaraki
 */
@RestController
@RequestMapping("/api")
public class CompilerController {
    
    private CompilerFacade compiler;
    
    /**
     * Instantiates a new Compiler controller.
     *
     * @param compiler the compiler
     */
    public CompilerController(CompilerFacade compiler) {
        this.compiler = compiler;
    }
    
    /**
     * Take as a parameter a json object
     *
     * @param request object
     * @param userId  the user id
     * @param prefer  the prefer push
     * @param url     the url
     * @return The statusResponse of the execution (Accepted, Wrong Answer, Time Limit Exceeded, Memory Limit Exceeded, Compilation Error, RunTime Error)
     * @throws IOException the io exception
     */
    @PostMapping("/compile/json")
    @ApiOperation(
            value = "Json",
            notes = "You should provide outputFile, inputFile (not required), source code, time limit and memory limit",
            response = Response.class
    )
    public ResponseEntity<Object> compile(@ApiParam(value = "request") @RequestBody Request request,
                                          @RequestHeader(value = WellKnownParams.USER_ID, required = false) String userId,
                                          @RequestHeader(value = WellKnownParams.PREFER, required = false) String prefer,
                                          @RequestHeader(value = WellKnownParams.URL, required = false) String url)
            throws IOException {
        
        Execution execution = ExecutionFactory.createExecution(
                request.getSourcecodeFile(),
                request.getConvertedTestCases(),
                request.getTimeLimit(),
                request.getMemoryLimit(),
                request.getLanguage());
        
        // Free memory space
        request = null;
        
        boolean isLongRunning = WellKnownHeaders.PREFER_PUSH.equals(prefer);
    
        return compiler.compile(execution, isLongRunning, url, userId);
    }
    
    /**
     * Compiler Controller
     *
     * @param language        the programming language
     * @param sourceCode      Python source code
     * @param inputs          the inputs
     * @param expectedOutputs the expected outputs
     * @param timeLimit       Time limit of the execution, must be between 0 and 15 sec
     * @param memoryLimit     Memory limit of the execution, must be between 0 and 1000 MB
     * @param prefer          the prefer push
     * @param url             the url
     * @param userId          the user id
     * @return The statusResponse of the execution (Accepted, Wrong Answer, Time Limit Exceeded, Memory Limit Exceeded, Compilation Error, RunTime Error)
     * @throws IOException the io exception
     */
    @PostMapping("/compile")
    @ApiOperation(
            value = "Multipart request",
            notes = "You should provide outputFile, inputFile (not required), source code, time limit and memory limit "
                    + "and the language",
            response = Response.class
    )
    public ResponseEntity compile(
            @ApiParam(value = "The language")
            @RequestParam(value = WellKnownParams.LANGUAGE) Language language,
        
            @ApiParam(value = "Your source code")
            @RequestPart(value = WellKnownParams.SOURCE_CODE) MultipartFile sourceCode,
        
            @ApiParam(value = "Inputs")
            @RequestParam(value = WellKnownParams.INPUTS, required = false) MultipartFile inputs,

            @ApiParam(value = "Expected outputs")
            @RequestParam(value = WellKnownParams.EXPECTED_OUTPUTS) MultipartFile expectedOutputs,
        
            @ApiParam(value = "The time limit in seconds that the execution must not exceed")
            @RequestParam(value = WellKnownParams.TIME_LIMIT) int timeLimit,
        
            @ApiParam(value = "The memory limit in MB that the execution must not exceed")
            @RequestParam(value = WellKnownParams.MEMORY_LIMIT) int memoryLimit,
        
            @RequestHeader(value = WellKnownParams.PREFER, required = false) String prefer,
            
            @RequestHeader(value = WellKnownParams.URL, required = false) String url,

            @RequestHeader(value = WellKnownParams.USER_ID, required = false) String userId)
            
            throws IOException {
        
        ConvertedTestCase testCase = new ConvertedTestCase("defaultTestId", inputs, expectedOutputs);
        
        Execution execution = ExecutionFactory.createExecution(
                sourceCode,
                List.of(testCase),
                timeLimit,
                memoryLimit,
                language);
        
        boolean isLongRunning = WellKnownHeaders.PREFER_PUSH.equals(prefer);
    
        return compiler.compile(execution, isLongRunning, url, userId);
    }
}
