<template>
  <div>
    <div>账号切换</div>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <button @click="zzjl()" type="default" size="mini">张总经理</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjU0sTAy0lEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQBzBfs9QgAAAA.zlVYnSCqSt462N0LJ2trehZN0xUXyE1Cd_IBINtislavGFjlGsk8L6bupyQCCre7TAS-cwAX0TDyN5tFae9dgQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjUEQiMdpdLi1CLPFCUrcwgzLzE3FailKiMxL704MU-pFgCYtjObRQAAAA.g7t7LylCtXIr5rWdE_yivaYqat0-WPBbdafcMGEvl6GpZ5xcmWePegedGv4lkO57TEWJGVbQLqB1BVCBc2KZsQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjU0NTK10FEqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagFUJjUfQQAAAA.o1N7_eeXvFU3Jndr77PbAb_EUgmXsHbQVpbJZ_6FiDHdL64-SvWCcJXywUTVFY2Z9EdPBtkEoXTUYLBIZpe1QQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjU0NTQz1VEqLU4t8kxRsjKBMPMSc1OBWnIyizOVagGPfwhzQQAAAA._mXOg86rolyTqjEkCdlSoOYvdqtmGuEHP960XDHWX9iVq-7hLXsATz9fS--F4nJS76h4dH8oqKl3nHmx6tvBiQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
