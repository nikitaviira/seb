import {Plugin} from 'chart.js';

const crosshairPlugin: Plugin = {
  id: 'crosshair',
  defaults: {
    width: 1,
    color: '#FF4949',
  },
  afterInit: (chart: any, args: any, opts: any) => {
    chart.crosshair = {
      x: 0,
      y: 0,
    };
  },
  afterEvent: (chart: any, args: any) => {
    const {inChartArea} = args;
    const {x, y} = args.event;

    chart.crosshair = {x, y, draw: inChartArea};
    chart.draw();
  },
  beforeDatasetsDraw: (chart: any, args: any, opts: any) => {
    const {ctx} = chart;
    const {top, bottom} = chart.chartArea;
    const {x, draw} = chart.crosshair;
    if (!draw) return;

    ctx.save();

    ctx.beginPath();
    ctx.lineWidth = opts.width;
    ctx.strokeStyle = opts.color;
    ctx.moveTo(x, bottom);
    ctx.lineTo(x, top);
    ctx.stroke();

    ctx.restore();
  },
};

export default crosshairPlugin;
