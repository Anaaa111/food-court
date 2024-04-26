package com.food.service;

import com.food.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook
     */
    void saveAddress(AddressBook addressBook);

    /**
     * 条件动态查询
     * @return
     */
    List<AddressBook> list();

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void updateAdress(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     * 查询默认地址
     * @return
     */
    List<AddressBook> getDefaultAddress();
}
