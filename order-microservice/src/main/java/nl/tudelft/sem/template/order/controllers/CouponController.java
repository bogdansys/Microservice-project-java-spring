package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import nl.tudelft.sem.template.order.domain.coupon.ApplyCouponService;
import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import nl.tudelft.sem.template.order.domain.coupon.CreateCouponService;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.CouponDoesNotExistException;
import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.ApplyCouponModel;
import nl.tudelft.sem.template.order.model.CreateCouponRequestModel;
import nl.tudelft.sem.template.order.model.DeleteCouponModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * The coupon controller, used to create and apply coupons
 */
@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final transient AuthManager authManager;
    private final transient CreateCouponService createCouponService;
    private final transient ApplyCouponService applyCouponService;
    private final transient StoreIdAdapter storeIdAdapter;

    /**
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param createCouponService The coupon service that is used to create coupons
     * @param applyCouponService
     */
    @Autowired
    public CouponController(AuthManager authManager, CreateCouponService createCouponService,
                            ApplyCouponService applyCouponService, StoreIdAdapter storeIdAdapter) {
        this.authManager = authManager;
        this.createCouponService = createCouponService;
        this.applyCouponService = applyCouponService;
        this.storeIdAdapter = storeIdAdapter;
    }

    /**
     * Endpoint for adding a coupon
     * @param request The create coupon request model
     * @param token The authorization token, used to pass to getUsersStoreId endpoint
     * @return the id of the coupon if successful
     */
    //PMD has an unfounded problem with storeId because of the try catches, so it's suppressed
    @SuppressWarnings("PMD")
    @PostMapping("/addCoupon")
    public ResponseEntity<String> addCoupon(@RequestBody CreateCouponRequestModel request,
                                    @RequestHeader("Authorization") String token) {

        //checks if user is a customer, if so, they cannot create coupons
        if(authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to add a coupon");
        }

        int storeId;
        if(authManager.getRole().equals("STORE_OWNER")){
            //if user is a store owner, then their store id must be taken from the user microservice
            try{
                storeId = storeIdAdapter.getUsersStoreId(token);
            }
            catch(Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        else{
            //if user is a regional manager then the storeId is -1, meaning coupon applies to all stores
            storeId = -1;
        }
        try {
            ActivationCode activationCode = new ActivationCode(request.getActivationCode());
            Coupon coupon = createCouponService.addCoupon(activationCode, request.getCouponType(),
                    request.getDiscountPercent(), request.getExpirationDate(), storeId);
            return ResponseEntity.ok().body(coupon.toString());
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint for deleting a coupon
     * @param request the delete coupon request model - only takes coupon id
     * @return 200 OK if successful
     */
    @DeleteMapping("/deleteCoupon")
    public ResponseEntity deleteCoupon(@RequestBody DeleteCouponModel request) {
        if(authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to delete coupons");
        }

        try {
            createCouponService.deleteCouponById(request.getCouponId());
        } catch (CouponDoesNotExistException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gets all the coupons in the database repository - used for debugging
     * @return all the coupon strings in a list
     */
    @GetMapping("/getCoupons")
    public ResponseEntity<String> getCoupons() {
        //Users can't see all the coupons cause that's usually not how coupons work.
        if (authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to access all coupons");
        }
        return ResponseEntity.ok(createCouponService.listAllCoupons().toString());
    }

    /**
     * Checks the validity of activation code and apply the coupon if it is a valid code
     */
    @PostMapping("/applyCoupon")
    public ResponseEntity<Double> applyCoupon(@RequestBody ApplyCouponModel applyCouponModel) {
        if(authManager.getRole().equals("ADMIN") || authManager.getRole().equals("STORE_OWNER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admins and Store Owners are" +
                    " not authorized to apply a coupon");
        }

        try {
            return ResponseEntity.ok(applyCouponService.applyCoupon(applyCouponModel.getActivationCode(), applyCouponModel.getOrderId()));
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
