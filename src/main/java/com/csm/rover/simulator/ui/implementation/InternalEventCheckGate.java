package com.csm.rover.simulator.ui.implementation;

class InternalEventCheckGate {
	
	static Builder responseWith(Runnable response){
		return new Builder(response);
	}

	static class Builder {
		
		private final Runnable response;
		
		private Builder(Runnable response){
			this.response = response;
		}
		
		public MenuCommandListener forAction(MenuCommandEvent.Action menuAction){
			return new MenuCommandListener(){
				@Override
				public void menuAction(MenuCommandEvent e) {
					if (e.getAction() == menuAction){
						response.run();
					}
				}
			};
		}
		
		public EmbeddedFrameListener forAction(EmbeddedFrameEvent.Action frameAction){
			return new EmbeddedFrameListener(){
				@Override
				public void frameChanged(EmbeddedFrameEvent e) {
					if (e.getAction() == frameAction){
						response.run();
					}
				}
			};
		}
		
	}
	
}
