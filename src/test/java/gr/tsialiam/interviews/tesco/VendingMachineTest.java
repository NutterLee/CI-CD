package gr.tsialiam.interviews.tesco;

import gr.tsialiam.interviews.tesco.exceptions.*;
import gr.tsialiam.interviews.tesco.model.Coin;
import gr.tsialiam.interviews.tesco.model.Item;
import gr.tsialiam.interviews.tesco.model.Outputter;
import gr.tsialiam.interviews.tesco.model.bank.Bank;
import gr.tsialiam.interviews.tesco.model.bank.FakeBank;
import gr.tsialiam.interviews.tesco.model.storage.Storage;
import gr.tsialiam.interviews.tesco.model.storage.StorageImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VendingMachineTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    //初始时售货机应当保持关闭的状态
    @Test
    public void shouldBeOffByDefault() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());

        assertThat(testee.isOn(), is(false));
    }

    //初始时售货机应当默认没有选择商品
    @Test
    public void shouldNotHaveSelectedItemByDefault() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());

        assertThat(testee.getSelectedItem(), is(nullValue()));
    }

    //按下开机键后售货机应当开机
    @Test
    public void shouldTurnOn() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();

        assertThat(testee.isOn(), is(true));
    }

    //按下关机键后售货机应当关机
    @Test
    public void shouldTurnOff() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();
        testee.setOff();

        assertThat(testee.isOn(), is(false));
    }

    //关机时应当退出售货机中的钱
    @Test
    public void shouldReturnMoneyWhenTurnOff() {
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());
        testee.insertCoin(Coin.ONE_POUND.getId());
        testee.setOff();

        verify(outputter, times(1)).returnCoin(Arrays.asList(Coin.FIFTY_PENCE, Coin.ONE_POUND));
    }

    //关机时应当退出售货机中的商品与硬币
    @Test
    public void shouldReturnItemsAndCoinsWhenTurnOff() {
        Outputter outputter = mock(Outputter.class);
        Storage storage = mock(Storage.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), storage);
        testee.setOn();
        Item anyAvailableItem = Item.C;
        testee.selectItem(anyAvailableItem);
        testee.insertCoin(Coin.TEN_PENCE.getId());
        testee.setOff();

        assertThat(testee.getSelectedItem(), is(nullValue()));
        assertThat(testee.isOn(), is(false));
        verify(outputter, times(1)).returnCoin(Collections.singletonList(Coin.TEN_PENCE));
        InOrder order = Mockito.inOrder(storage);
        order.verify(storage).remove(anyAvailableItem);
    }


    //当售货机关机时，投入硬币应当报错
    @Test
    public void shouldThrownExceptionIfCoinInsertedWhenMachineIsOff() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());

        expectedException.expect(VendingMachineIllegalStateException.class);
        testee.insertCoin(Coin.TEN_PENCE.getId());
    }

    //购买商品时若投入无法识别的硬币应当报错
    @Test
    public void shouldThrowExceptionIfUnrecognizedCoinWasInserted() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();
        String unrecognizedCoin = "$10";

        expectedException.expect(VendingMachineUnrecognizedCoinException.class);
        expectedException.expectMessage("Unable to recognise coin " + unrecognizedCoin);
        testee.insertCoin(unrecognizedCoin);
    }

    //当售货机关机时，按退钱按钮应当报错
    @Test
    public void shouldThrowExceptionIfPressReturnMoneyWhenMachineIsOff() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());

        expectedException.expect(VendingMachineIllegalStateException.class);
        testee.returnMoney();
    }

    //购买商品时，投入硬币以后不想买了，按退钱按钮应当退出所投入的钱
    @Test
    public void shouldReturnInsertedCoinsWhenPressReturnMoney() {
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());
        testee.insertCoin(Coin.TEN_PENCE.getId());
        testee.returnMoney();

        verify(outputter, times(1)).returnCoin(Arrays.asList(Coin.FIFTY_PENCE, Coin.TEN_PENCE));
        assertThat(testee.getSelectedItem(), is(nullValue()));
    }

    //售货机关机时，若选择商品应该报错
    @Test
    public void shouldThrowExceptionIfSelectProductWhenMachineIsOff() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());

        expectedException.expect(VendingMachineIllegalStateException.class);
        testee.selectItem(Item.A);
    }


    //购买商品时，若售货机发现无法找零应该报错并退还所投入的硬币
    @Test
    public  void shouldThrowExceptionIfInsufficientFundsInMachine(){

        Map<Coin, Integer> money = new EnumMap<>(Coin.class);
        money.put(Coin.TEN_PENCE, 0);
        money.put(Coin.TWENTY_PENCE, 0);
        money.put(Coin.FIFTY_PENCE, 0);
        money.put(Coin.ONE_POUND, 0);
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, new FakeBank(money), createAStorage());

        testee.setOn();
        testee.insertCoin(Coin.ONE_POUND.getId());
        expectedException.expect(VendingMachineInsufficientFundsException.class);
        testee.selectItem(Item.A);

    }

    //购买商品时，若所选的项目已经售空，应当报错
    @Test
    public void shouldThrowExceptionIfSelectedItemIsUnavailable(){
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();
        Item anyUnavailableItem = Item.B;

        expectedException.expect(VendingMachineInsufficientStockException.class);
        expectedException.expectMessage("Insufficient stock " + anyUnavailableItem);
        testee.selectItem(anyUnavailableItem);
    }

    /*
    //购买商品时，钱不够
    @Test
    public void shouldThrowExceptionIfMoneyIsInsufficient(){
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());

        expectedException.expect(VendingMachineInsufficientMoney.class);
        testee.selectItem(Item.A);

    }*/


    //购买商品时，投入的硬币数量刚好与商品价格相同时，应当给出商品并增加余额
    @Test
    public void shouldVendSelectedItemWhenSelectItemAndInsertedCoinsAreExactlyPrice(){
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());
        testee.insertCoin(Coin.TEN_PENCE.getId());
        testee.selectItem(Item.A);

        verify(outputter, times(1)).vendItem(Item.A);
    }

    //购买商品时，若投入的钱高于商品价格，应当给出商品，找零并增加余额
    @Test
    public void shouldVendSelectedItemAndReturnChangeWhenSelectItemAndInsertedCoinsAreMoreThanPrice(){
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());
        testee.insertCoin(Coin.ONE_POUND.getId());
        testee.selectItem(Item.A);
        verify(outputter).vendItem(Item.A);
        outputter.vendItem(Item.A);
        ArgumentCaptor<List<Coin>> argument = ArgumentCaptor.forClass((Class)List.class);
        verify(outputter).returnCoin(argument.capture());
        List<Coin> charge = argument.getValue();
        assertThat(charge, is(notNullValue()));
        assertThat(charge.stream().mapToInt(Coin::getValueInPence).sum(),is(90));
        assertThat(testee.getSelectedItem(), is(nullValue()));
    }

    //购买商品时，若投入的硬币总价高于商品价格，应当给出商品，找零并增加余额
    @Test
    public void shouldVendSelectedItemAndReturnChangeWhenInsertCoinsMoreThanPrice(){
        Outputter outputter = mock(Outputter.class);
        VendingMachine testee = new VendingMachine(outputter, createABank(), createAStorage());
        testee.setOn();
        testee.insertCoin(Coin.FIFTY_PENCE.getId());
        testee.selectItem(Item.C);
        testee.insertCoin(Coin.ONE_POUND.getId());
        testee.insertCoin(Coin.FIFTY_PENCE.getId());

        verify(outputter).vendItem(Item.C);
        ArgumentCaptor<List<Coin>> argument = ArgumentCaptor.forClass((Class)List.class);
        verify(outputter).returnCoin(argument.capture());
        List<Coin> charge = argument.getValue();
        assertThat(charge, is(notNullValue()));
        assertThat(charge.stream().mapToInt(Coin::getValueInPence).sum(),is(30));
        assertThat(testee.getSelectedItem(), is(nullValue()));
    }

    //购买商品时，若商品有库存，应当能成功选择该商品
    @Test
    public void shouldSelectItemIfAvailable() {
        VendingMachine testee = new VendingMachine(new Outputter(), createABank(), createAStorage());
        testee.setOn();
        testee.selectItem(Item.A);

        assertThat(testee.getSelectedItem(), is(Item.A));
    }

    //创建售货机的库存：包含两个A商品，一个C商品
    private static Storage createAStorage() {
        Map<Item, Integer> stock = new EnumMap<>(Item.class);
        stock.put(Item.A, 2);
        stock.put(Item.C, 1);

        return new StorageImpl(stock);
    }

    //创建售货机的硬币库存：包含5枚10元，5枚20元，5枚50元，5枚100元
    private static Bank createABank() {
        Map<Coin, Integer> money = new EnumMap<>(Coin.class);
        money.put(Coin.TEN_PENCE, 5);
        money.put(Coin.TWENTY_PENCE, 5);
        money.put(Coin.FIFTY_PENCE, 5);
        money.put(Coin.ONE_POUND, 5);

        return new FakeBank(money);
    }
}
