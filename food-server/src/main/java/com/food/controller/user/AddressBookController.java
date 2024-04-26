package com.food.controller.user;

import com.food.entity.AddressBook;
import com.food.result.Result;
import com.food.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址薄相关接口")
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result saveAddress(@RequestBody AddressBook addressBook){
        log.info("新增地址：{}", addressBook);
        addressBookService.saveAddress(addressBook);
        return Result.success();
    }

    /**
     * 查询当前用户的所有地址信息
     * @return
     */
    @GetMapping("list")
    @ApiOperation("查询当前用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        List<AddressBook> addressBookList = addressBookService.list();
        return Result.success(addressBookList);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook address = addressBookService.getById(id);
        return Result.success(address);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateAdress(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefaultAddress(){
        List<AddressBook> addressBookList = addressBookService.getDefaultAddress();
        if (addressBookList != null && addressBookList.size() > 0){
            return Result.success(addressBookList.get(0));
        }
        return Result.error("该用户没有默认地址");
    }

}
