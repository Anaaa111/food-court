package com.food.service.impl;

import com.food.context.BaseContext;
import com.food.entity.AddressBook;
import com.food.mapper.AddressBookMapper;
import com.food.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    public void saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        /**
         * 设置动态条件查询，可以进行复用
         */
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void updateAdress(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        // 设置默认地址，首先将所有地址的默认地址取消
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.unDefaultByUserId(addressBook);

        // 然后通过id修改默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public List<AddressBook> getDefaultAddress() {
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }
}
